# Swagger API 문서화 - FE/BE 분리 구조

## 🚀 접속 방법

### 기본 접속
- **루트 접속**: http://localhost:8080/ → Swagger UI 자동 리다이렉트
- **직접 접속**: http://localhost:8080/swagger-ui.html

### API 스펙 다운로드
- **전체 API**: http://localhost:8080/api-docs
- **인증 API**: http://localhost:8080/api-docs/auth  
- **공개 API**: http://localhost:8080/api-docs/public

## 🔧 FE/BE 분리 구조 특징

### 1. 세션 기반 인증
- 로그인 후 JSESSIONID 쿠키로 인증 상태 유지
- 프론트엔드에서 세션 쿠키를 자동으로 전송
- 별도의 JWT 토큰 관리 불필요

### 2. OAuth 플로우
```
1. FE → http://localhost:8080/oauth2/authorization/github
2. GitHub OAuth 인증
3. http://localhost:8080/api/auth/success 리다이렉트
4. 세션 생성 및 사용자 정보 반환
```

### 3. API 구조
- **인증 API** (`/api/auth/**`): 로그인 필요
- **공개 API** (`/api/public/**`): 인증 불필요
- **문서 API** (`/swagger-ui/**`, `/api-docs/**`): 공개

## 📱 프론트엔드 연동 예시

### JavaScript (Fetch API)
```javascript
// 1. 로그인 상태 확인
async function checkAuthStatus() {
    const response = await fetch('/api/auth/status', {
        credentials: 'include' // 쿠키 포함
    });
    return await response.json();
}

// 2. 로그인 시작
function startLogin() {
    window.location.href = '/oauth2/authorization/github';
}

// 3. 사용자 정보 조회
async function getCurrentUser() {
    const response = await fetch('/api/auth/me', {
        credentials: 'include'
    });
    if (response.ok) {
        return await response.json();
    }
    throw new Error('Not authenticated');
}

// 4. 로그아웃
async function logout() {
    const response = await fetch('/api/auth/logout', {
        method: 'POST',
        credentials: 'include'
    });
    return await response.json();
}
```

### React 예시
```jsx
// AuthContext.js
import { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext();

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        checkAuthStatus();
    }, []);

    const checkAuthStatus = async () => {
        try {
            const response = await fetch('/api/auth/me', {
                credentials: 'include'
            });
            if (response.ok) {
                const userData = await response.json();
                setUser(userData);
            }
        } catch (error) {
            console.error('Auth check failed:', error);
        } finally {
            setLoading(false);
        }
    };

    const login = () => {
        window.location.href = '/oauth2/authorization/github';
    };

    const logout = async () => {
        try {
            await fetch('/api/auth/logout', {
                method: 'POST',
                credentials: 'include'
            });
            setUser(null);
        } catch (error) {
            console.error('Logout failed:', error);
        }
    };

    return (
        <AuthContext.Provider value={{ user, login, logout, loading }}>
            {children}
        </AuthContext.Provider>
    );
}

export const useAuth = () => useContext(AuthContext);
```

## 🔍 API 테스트 방법

### 1. Swagger UI에서 테스트
1. http://localhost:8080/ 접속
2. 공개 API 먼저 테스트 (`/api/public/health`)
3. 브라우저에서 GitHub 로그인 완료
4. 인증 API 테스트 (`/api/auth/me`)

### 2. cURL로 테스트
```bash
# 공개 API 테스트
curl -X GET "http://localhost:8080/api/public/health"

# 로그인 후 세션 쿠키로 인증 API 테스트
curl -X GET "http://localhost:8080/api/auth/me" \
     -H "Cookie: JSESSIONID=your_session_id" \
     -c cookies.txt -b cookies.txt
```

### 3. Postman 테스트
1. OpenAPI JSON을 Postman에 import
2. Settings → Cookies → Domain: localhost 추가
3. 브라우저에서 로그인 후 쿠키 복사하여 사용

## 🛠 개발 환경 설정

### 프론트엔드 개발 시 CORS 설정
application.yaml에 이미 CORS가 설정되어 있어 별도 설정 불필요:
```yaml
# SecurityConfig.java에서 자동 처리
corsConfiguration.setAllowedOriginPatterns(List.of("*"));
corsConfiguration.setAllowCredentials(true);
```

### 환경별 설정
```bash
# 개발 환경
export GITHUB_CLIENT_ID=dev_client_id
export GITHUB_CLIENT_SECRET=dev_client_secret

# 운영 환경  
export GITHUB_CLIENT_ID=prod_client_id
export GITHUB_CLIENT_SECRET=prod_client_secret
```

## 🔒 보안 고려사항

### 1. 세션 관리
- 세션 만료 시간: 기본 30분
- Redis 세션 저장소 사용 권장
- HTTPS 환경에서 Secure 쿠키 설정

### 2. CORS 정책
- 운영 환경에서는 특정 도메인만 허용
- `allowedOriginPatterns`를 구체적으로 설정

### 3. API 보안
- 인증이 필요한 모든 API는 `/api/auth/**` 경로 사용
- 민감한 정보는 응답에서 제외
- Rate Limiting 적용 권장

## 🎯 핵심 포인트

1. **간단한 인증**: 세션 기반으로 복잡한 토큰 관리 불필요
2. **실시간 테스트**: Swagger UI에서 바로 API 테스트 가능
3. **FE 친화적**: 표준 HTTP 쿠키 방식으로 모든 프론트엔드 프레임워크 지원
4. **완전한 문서화**: 모든 API 엔드포인트가 Swagger로 문서화됨

이제 프론트엔드 개발자가 쉽게 API를 이해하고 연동할 수 있는 완전한 백엔드 API가 준비되었습니다! 🚀
