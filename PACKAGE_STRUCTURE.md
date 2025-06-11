## 📦 Apple Playground - 패키지 구조

### 🎯 설계 원칙
- **global**: 전역적이고 공통적인 기능 (설정, 예외처리, 외부 API)
- **domain**: 비즈니스 도메인별 기능 (auth, article 등)

### 📁 패키지 구조

```
com.apple.appleplayground/
├── AppleplaygroundApplication.java
├── global/                              # 🌍 전역 공통 기능
│   ├── config/                         # ⚙️ 전역 설정
│   │   ├── JpaConfig.java             # JPA 설정
│   │   ├── SecurityConfig.java        # 보안 설정
│   │   └── SwaggerConfig.java         # API 문서 설정
│   ├── exception/                      # 🚨 전역 예외 처리
│   │   └── GlobalExceptionHandler.java
│   └── external/                       # 🔗 외부 API 클라이언트
│       └── GitHubApiClient.java       # GitHub API 연동
└── domain/                             # 🏢 비즈니스 도메인
    ├── auth/                          # 🔐 인증 도메인
    │   ├── controller/
    │   │   └── AuthController.java
    │   ├── service/
    │   │   ├── AuthService.java
    │   │   ├── CustomOAuth2UserService.java
    │   │   └── UserService.java
    │   ├── entity/
    │   │   ├── User.java
    │   │   └── Role.java
    │   ├── repository/
    │   │   └── UserRepository.java
    │   └── dto/
    │       ├── UserPrincipal.java
    │       ├── GitHubUserDto.java
    │       ├── UserResponseDto.java
    │       ├── request/
    │       └── response/
    │           ├── AuthMeResponse.java
    │           ├── AuthStatusResponse.java
    │           └── LogoutResponse.java
    ├── article/                       # 📝 게시글 도메인 (미래 확장)
    └── common/                        # 🔧 도메인 공통
        └── PublicApiController.java   # 공통 API
```

### 🎯 패키지별 책임

#### global 패키지
- **config**: Spring 설정 클래스들 (Security, JPA, Swagger)
- **exception**: 전역 예외 처리
- **external**: 외부 API 클라이언트 (GitHub, 결제 등)

#### domain 패키지
- **auth**: 사용자 인증/인가 관련 모든 기능
- **article**: 게시글 관련 기능 (향후 확장)
- **common**: 도메인 간 공통 API

### 🚀 확장성
새로운 도메인 추가 시:
```
domain/
├── payment/           # 결제 도메인
├── notification/      # 알림 도메인
└── admin/            # 관리자 도메인
```

각 도메인은 독립적으로 controller, service, entity, repository, dto 구조를 가집니다.
