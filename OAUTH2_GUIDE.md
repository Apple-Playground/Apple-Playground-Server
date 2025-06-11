# GitHub OAuth2 로그인 구현 가이드

## 개요

Apple Playground 프로젝트에서 GitHub OAuth2를 통한 소셜 로그인 기능을 구현한 가이드입니다.

## 아키텍처

```
Frontend (React/Vue/etc)     Backend (Spring Boot)      GitHub OAuth
     :3000              →        :8080             →      github.com
                               
1. 로그인 버튼 클릭  →  2. OAuth 시작 요청  →  3. GitHub 인증 페이지
   ↓                      ↓                      ↓
8. 인증 상태 확인   ←  7. 토큰 교환 완료   ←  4. Authorization Code 반환
   ↓                      ↓                      ↓
9. 사용자 정보 표시  ←  6. 사용자 정보 저장  ←  5. 사용자 정보 조회
```

## GitHub OAuth App 설정

### 1. GitHub에서 OAuth App 생성
1. GitHub → Settings → Developer settings → OAuth Apps
2. **New OAuth App** 클릭
3. 다음 정보 입력:
   ```
   Application name: Apple Playground
   Homepage URL: http://localhost:3000
   Authorization callback URL: http://localhost:8080/login/oauth2/code/github
   ```

### 2. 환경 변수 설정
`.env` 파일에 GitHub OAuth 정보 추가:
```env
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret
```

## 백엔드 구현

### 1. OAuth2 의존성 (이미 추가됨)
```gradle
implementation 'org.springframework.boot:spring-boot-starter-oauth2-client'
```

### 2. application.yml 설정
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          github:
            client-id: ${GITHUB_CLIENT_ID}
            client-secret: ${GITHUB_CLIENT_SECRET}
            scope:
              - user:email
              - read:user
```

### 3. 주요 엔드포인트

| 엔드포인트 | 메서드 | 설명 |
|-----------|---------|------|
| `/oauth2/authorization/github` | GET | OAuth 로그인 시작 |
| `/login/oauth2/code/github` | GET | GitHub 콜백 (자동 처리) |
| `/api/auth/me` | GET | 현재 사용자 정보 조회 |
| `/api/auth/status` | GET | 인증 상태 확인 |
| `/api/auth/logout` | POST | 로그아웃 |

## 프론트엔드 구현 가이드

### 1. 로그인 시작
```javascript
// 로그인 버튼 클릭 시
const handleGitHubLogin = () => {
  window.location.href = 'http://localhost:8080/oauth2/authorization/github';
};
```

### 2. 로그인 성공 처리
OAuth 성공 시 `/auth/success` 페이지로 리다이렉트됩니다.

```javascript
// /auth/success 페이지에서
useEffect(() => {
  // 백엔드에서 인증 상태 확인
  fetch('http://localhost:8080/api/auth/me', {
    method: 'GET',
    credentials: 'include' // 쿠키 포함
  })
  .then(response => response.json())
  .then(data => {
    if (data.authenticated) {
      console.log('로그인 성공:', data.user);
      // 대시보드 또는 메인 페이지로 이동
      navigate('/dashboard');
    } else {
      // 로그인 실패 처리
      navigate('/login?error=authentication_failed');
    }
  })
  .catch(error => {
    console.error('인증 확인 실패:', error);
    navigate('/login?error=network_error');
  });
}, []);
```

### 3. 로그인 실패 처리
OAuth 실패 시 `/auth/failure` 페이지로 리다이렉트됩니다.

```javascript
// /auth/failure 페이지에서
useEffect(() => {
  console.error('GitHub 로그인 실패');
  // 에러 메시지 표시 후 로그인 페이지로 이동
  setTimeout(() => {
    navigate('/login?error=oauth_failed');
  }, 3000);
}, []);
```

### 4. 인증 상태 확인
```javascript
// 인증이 필요한 페이지에서
const checkAuthStatus = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/auth/status', {
      credentials: 'include'
    });
    const data = await response.json();
    
    if (!data.authenticated) {
      navigate('/login');
    }
  } catch (error) {
    console.error('인증 상태 확인 실패:', error);
    navigate('/login');
  }
};
```

### 5. 로그아웃
```javascript
const handleLogout = async () => {
  try {
    await fetch('http://localhost:8080/api/auth/logout', {
      method: 'POST',
      credentials: 'include'
    });
    
    // 로그인 페이지로 이동
    navigate('/login');
  } catch (error) {
    console.error('로그아웃 실패:', error);
  }
};
```

## OAuth2 플로우 상세

### 1단계: 로그인 시작
```
Frontend → http://localhost:8080/oauth2/authorization/github
```

### 2단계: GitHub 리다이렉트
```
Spring Security → https://github.com/login/oauth/authorize?
  client_id=YOUR_CLIENT_ID&
  redirect_uri=http://localhost:8080/login/oauth2/code/github&
  scope=user:email+read:user&
  state=RANDOM_STATE
```

### 3단계: 사용자 인증 및 권한 승인
사용자가 GitHub에서 로그인하고 권한을 승인합니다.

### 4단계: Authorization Code 반환
```
GitHub → http://localhost:8080/login/oauth2/code/github?
  code=AUTHORIZATION_CODE&
  state=RANDOM_STATE
```

### 5단계: Access Token 교환
Spring Security가 자동으로 처리:
```
POST https://github.com/login/oauth/access_token
{
  "client_id": "YOUR_CLIENT_ID",
  "client_secret": "YOUR_CLIENT_SECRET",
  "code": "AUTHORIZATION_CODE"
}
```

### 6단계: 사용자 정보 조회
Spring Security가 자동으로 처리:
```
GET https://api.github.com/user
Authorization: Bearer ACCESS_TOKEN
```

### 7단계: 사용자 정보 저장
`CustomOAuth2UserService`에서 사용자 정보를 데이터베이스에 저장합니다.

### 8단계: 프론트엔드로 리다이렉트
```
Spring Security → http://localhost:3000/auth/success
```

## 보안 고려사항

### 1. CORS 설정
- 프론트엔드(`localhost:3000`)만 허용
- 쿠키 전송 허용(`credentials: include`)

### 2. 세션 관리
- JSESSIONID 쿠키를 통한 세션 관리
- 로그아웃 시 세션 무효화

### 3. 환경 변수
- GitHub Client Secret은 절대 프론트엔드에 노출하지 않음
- 환경 변수로 관리

## 테스트 방법

### 1. 로그인 테스트
1. 프론트엔드에서 로그인 버튼 클릭
2. GitHub 로그인 페이지로 리다이렉트 확인
3. GitHub 계정으로 로그인
4. 권한 승인 후 프론트엔드 성공 페이지로 이동 확인

### 2. API 테스트
```bash
# 인증 상태 확인
curl -X GET "http://localhost:8080/api/auth/status" \
  -H "Cookie: JSESSIONID=YOUR_SESSION_ID"

# 사용자 정보 조회
curl -X GET "http://localhost:8080/api/auth/me" \
  -H "Cookie: JSESSIONID=YOUR_SESSION_ID"
```

## 트러블슈팅

### 1. CORS 에러
- `corsConfigurationSource()` 설정 확인
- 프론트엔드에서 `credentials: 'include'` 설정 확인

### 2. GitHub OAuth 에러
- Client ID, Client Secret 확인
- Callback URL이 정확한지 확인

### 3. 세션 문제
- 쿠키가 올바르게 설정되는지 브라우저 개발자 도구에서 확인
- 로그아웃 후 세션이 무효화되는지 확인

## 참고 자료

- [Spring Security OAuth2 공식 문서](https://docs.spring.io/spring-security/reference/servlet/oauth2/login/index.html)
- [GitHub OAuth Apps 문서](https://docs.github.com/en/developers/apps/building-oauth-apps)
