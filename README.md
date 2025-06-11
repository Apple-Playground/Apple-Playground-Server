# Apple Playground - GitHub OAuth Implementation

## 📖 개요

Spring Boot 3.4.6 기반의 GitHub OAuth 인증 시스템입니다. Feign Client를 사용하여 GitHub API와 통신하며, 사용자 정보를 데이터베이스에 저장하고 관리합니다.

## 🚀 주요 기능

- GitHub OAuth 2.0 소셜 로그인
- Feign Client를 통한 GitHub API 연동
- 사용자 정보 자동 동기화
- Spring Security 기반 인증/인가
- JPA를 통한 사용자 데이터 관리
- Redis 세션 관리 지원

## 🛠 기술 스택

- **Backend**: Spring Boot 3.4.6, Spring Security, Spring Data JPA
- **Database**: MySQL 8.0
- **Cache**: Redis
- **External API**: GitHub API (Feign Client)
- **Frontend**: Thymeleaf, Bootstrap 5
- **Build Tool**: Gradle

## 📋 사전 요구사항

1. **GitHub OAuth App 생성**
   - GitHub → Settings → Developer settings → OAuth Apps
   - Application name: `Apple Playground`
   - Homepage URL: `http://localhost:8080`
   - Authorization callback URL: `http://localhost:8080/login/oauth2/code/github`

2. **환경 변수 설정**
   ```bash
   export GITHUB_CLIENT_ID=your_github_client_id
   export GITHUB_CLIENT_SECRET=your_github_client_secret
   export DB_USERNAME=root
   export DB_PASSWORD=your_db_password
   export REDIS_PASSWORD=your_redis_password  # 선택사항
   ```

3. **데이터베이스 설정**
   ```sql
   CREATE DATABASE appleplayground;
   ```

## 🏃‍♂️ 실행 방법

1. **의존성 설치 및 빌드**
   ```bash
   ./gradlew build
   ```

2. **애플리케이션 실행**
   ```bash
   ./gradlew bootRun
   ```

3. **브라우저에서 접속**
   ```
   http://localhost:8080
   ```

## 📚 API 문서

### Swagger UI
- **URL**: http://localhost:8080/swagger-ui.html
- **기능**: 인터랙티브 API 문서
- **테스트**: 브라우저에서 직접 API 호출 가능
- **루트 접속**: http://localhost:8080/ 에서 Swagger UI 자동 리다이렉트

### OpenAPI Specification
- **JSON**: http://localhost:8080/api-docs
- **그룹별 API**: 
  - 인증 API: http://localhost:8080/api-docs/auth
  - 공개 API: http://localhost:8080/api-docs/public

### API 그룹

#### 🔐 인증 API (`/api/auth`)
- 인증이 필요한 사용자 관련 API
- 세션 기반 인증 또는 OAuth2 토큰 필요

#### 🌍 공개 API (`/api/public`)
- 인증이 필요하지 않은 공개 API
- 헬스 체크, 애플리케이션 정보 등

### API 테스트 방법

1. **Swagger UI 사용**
   ```
   1. http://localhost:8080/swagger-ui.html 접속
   2. 원하는 API 엔드포인트 선택
   3. "Try it out" 클릭
   4. 필요한 파라미터 입력
   5. "Execute" 클릭하여 실행
   ```

2. **cURL 사용**
   ```bash
   # 공개 API 테스트
   curl -X GET "http://localhost:8080/api/public/health"
   
   # 인증 필요 API (로그인 후 세션 쿠키 사용)
   curl -X GET "http://localhost:8080/api/auth/me" \
        -H "Cookie: JSESSIONID=your_session_id"
   ```

3. **Postman 컬렉션**
   - OpenAPI JSON을 Postman으로 import 가능
   - http://localhost:8080/api-docs에서 JSON 다운로드

## 📋 API 엔드포인트

### 인증 관련 API
- `POST /oauth2/authorization/github` - GitHub OAuth 로그인 시작
- `GET /login/oauth2/code/github` - GitHub OAuth 콜백
- `GET /api/auth/success` - 로그인 성공 후 리다이렉트
- `GET /api/auth/failure` - 로그인 실패 후 리다이렉트
- `POST /api/auth/logout` - 로그아웃
- `GET /api/auth/logout-success` - 로그아웃 완료

### 🔐 인증 API (`/api/auth`)
- `GET /api/auth/me` - 현재 사용자 정보 조회 (인증 필요)
- `GET /api/auth/status` - 인증 상태 확인
- `GET /api/auth/user/{username}` - 사용자명으로 사용자 조회
- `GET /api/auth/success` - OAuth 로그인 성공 처리
- `GET /api/auth/failure` - OAuth 로그인 실패 처리

### 🌍 공개 API (`/api/public`)
- `GET /api/public/health` - 헬스 체크
- `GET /api/public/info` - 애플리케이션 정보

### 📖 API 문서
- `GET /` - Swagger UI 홈 (자동 리다이렉트)
- `GET /swagger-ui.html` - Swagger UI (인터랙티브 API 문서)
- `GET /api-docs` - OpenAPI 3.0 JSON 스펙
- `GET /api-docs/{group}` - 그룹별 API 문서 (auth, public)

## 🗂 프로젝트 구조

```
src/main/java/com/apple/appleplayground/
├── config/                         # 설정 클래스
│   ├── SecurityConfig.java         # Spring Security 설정
│   └── JpaConfig.java             # JPA Auditing 설정
├── controller/                     # 페이지 컨트롤러
│   └── PageController.java        # 메인 페이지 라우팅
├── domain/auth/                    # 인증 도메인
│   ├── client/                    # 외부 API 클라이언트
│   │   └── GitHubApiClient.java   # GitHub API Feign Client
│   ├── controller/                # REST API 컨트롤러
│   │   └── AuthController.java    # 인증 관련 API
│   ├── dto/                       # 데이터 전송 객체
│   │   ├── GitHubUserDto.java     # GitHub 사용자 DTO
│   │   └── UserResponseDto.java   # 사용자 응답 DTO
│   ├── entity/                    # JPA 엔티티
│   │   ├── User.java              # 사용자 엔티티
│   │   └── Role.java              # 역할 열거형
│   ├── repository/                # 데이터 접근 계층
│   │   └── UserRepository.java    # 사용자 리포지토리
│   └── service/                   # 비즈니스 로직
│       ├── CustomOAuth2UserService.java  # OAuth2 사용자 서비스
│       └── UserService.java       # 사용자 서비스
├── exception/                      # 예외 처리
│   └── GlobalExceptionHandler.java # 전역 예외 핸들러
└── AppleplaygroundApplication.java # 메인 애플리케이션 클래스
```

## 🔐 보안 설정

- **CSRF**: 비활성화 (REST API 용도)
- **OAuth2 Login**: GitHub 제공자 사용
- **세션 관리**: Redis 기반 (설정 시)
- **권한 관리**: Role 기반 (USER, ADMIN)

## 🔧 설정 파일

### application.yaml
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

## 💾 데이터베이스 스키마

### users 테이블
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    github_id VARCHAR(255) UNIQUE NOT NULL,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255),
    avatar_url VARCHAR(500),
    location VARCHAR(255),
    company VARCHAR(255),
    bio TEXT,
    blog VARCHAR(500),
    public_repos INT,
    followers INT,
    following INT,
    role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

## 🧪 테스트 방법

1. **홈페이지 접속**: http://localhost:8080
2. **로그인 버튼 클릭**: GitHub OAuth 페이지로 리다이렉트
3. **GitHub 계정으로 로그인**: 권한 승인
4. **대시보드 확인**: 사용자 정보 및 API 테스트 가능

## 🐛 트러블슈팅

### 일반적인 문제

1. **OAuth 콜백 오류**
   - GitHub OAuth App의 콜백 URL 확인
   - `http://localhost:8080/login/oauth2/code/github`

2. **데이터베이스 연결 오류**
   - MySQL 서버 실행 상태 확인
   - 데이터베이스 및 사용자 권한 확인

3. **환경 변수 오류**
   - GITHUB_CLIENT_ID, GITHUB_CLIENT_SECRET 설정 확인

## 📝 추가 개발 가능 기능

- [ ] JWT 토큰 기반 인증
- [ ] 사용자 프로필 편집
- [ ] GitHub 리포지토리 연동
- [ ] 관리자 페이지
- [x] **API 문서화 (Swagger)**
- [ ] 소셜 로그인 확장 (Google, Naver 등)
- [ ] 사용자 활동 로그
- [ ] 이메일 인증
- [ ] API Rate Limiting
- [ ] 데이터 억세스 로그

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.
