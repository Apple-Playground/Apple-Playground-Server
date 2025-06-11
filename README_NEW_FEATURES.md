# Apple Playground - 새로운 기능 가이드

## 📋 추가된 기능

### 1. 팔로우 시스템
- 사용자 간 팔로우/언팔로우 기능
- 팔로워/팔로잉 목록 조회
- 팔로우 상태 확인

### 2. 블로그 시스템
- 블로그 포스트 작성, 수정, 삭제
- 포스트 목록 조회 (전체/사용자별)
- 포스트 검색 (제목/내용)
- 무제한 좋아요 시스템
- 조회수 추적

### 3. 이미지 관리 (AWS S3)
- **비동기 이미지 업로드** (5MB 이상 파일 자동 비동기 처리)
- **Pre-signed URL** 지원 (클라이언트 직접 업로드)
- 이미지 메타데이터 관리
- 사용자별 이미지 목록 조회
- 임시 다운로드 URL 생성

## 🔧 설정 방법

### 1. 환경변수 설정
`.env.example` 파일을 `.env`로 복사하고 다음 값들을 설정하세요:

```bash
# AWS S3 설정
AWS_S3_ACCESS_KEY=your_aws_access_key_here
AWS_S3_SECRET_KEY=your_aws_secret_key_here
AWS_S3_BUCKET_NAME=your_s3_bucket_name_here
AWS_S3_REGION=ap-northeast-2
```

### 2. AWS S3 버킷 설정

#### a) S3 버킷 생성
```bash
# AWS CLI로 버킷 생성
aws s3 mb s3://your-bucket-name --region ap-northeast-2
```

#### b) IAM 사용자 생성 및 권한 설정
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "s3:GetObject",
                "s3:PutObject",
                "s3:DeleteObject",
                "s3:PutObjectAcl"
            ],
            "Resource": "arn:aws:s3:::your-bucket-name/*"
        },
        {
            "Effect": "Allow",
            "Action": [
                "s3:ListBucket"
            ],
            "Resource": "arn:aws:s3:::your-bucket-name"
        }
    ]
}
```

#### c) S3 CORS 설정 (필수)
```json
[
    {
        "AllowedHeaders": ["*"],
        "AllowedMethods": ["GET", "PUT", "POST", "DELETE"],
        "AllowedOrigins": ["http://localhost:3000", "https://yourdomain.com"],
        "ExposeHeaders": ["ETag"]
    }
]
```

#### d) 공개 읽기 권한 설정 (선택사항)
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "PublicReadGetObject",
            "Effect": "Allow",
            "Principal": "*",
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::your-bucket-name/*"
        }
    ]
}
```

### 3. 데이터베이스 스키마
새로운 테이블들이 자동으로 생성됩니다:
- `follows` - 팔로우 관계
- `blog_posts` - 블로그 포스트
- `images` - 이미지 정보

기존 `users` 테이블에 다음 컬럼들이 추가됩니다:
- 연락처 정보 (`phone_number`, `contact1-4`, `linkedin_url`, `github_profile_url`)
- 팔로우 수 캐시 (`followers_count`, `following_count`)

## 📚 API 문서

### 팔로우 API

#### 사용자 팔로우
```http
POST /api/follow/{userId}
Authorization: Bearer {token}
```

#### 사용자 언팔로우
```http
DELETE /api/follow/{userId}
Authorization: Bearer {token}
```

#### 팔로우 상태 확인
```http
GET /api/follow/status/{userId}
Authorization: Bearer {token}
```

#### 팔로워 목록 조회
```http
GET /api/follow/followers/{userId}?page=0&size=20
```

#### 팔로잉 목록 조회
```http
GET /api/follow/following/{userId}?page=0&size=20
```

### 블로그 API

#### 포스트 작성
```http
POST /api/blog/posts
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "포스트 제목",
  "content": "포스트 내용"
}
```

#### 포스트 조회
```http
GET /api/blog/posts/{postId}
```

#### 전체 포스트 목록
```http
GET /api/blog/posts?page=0&size=20
```

#### 사용자별 포스트 목록
```http
GET /api/blog/posts/user/{userId}?page=0&size=20
```

#### 포스트 검색
```http
GET /api/blog/posts/search?keyword=검색어&page=0&size=20
```

#### 포스트 수정
```http
PUT /api/blog/posts/{postId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "수정된 제목",
  "content": "수정된 내용"
}
```

#### 포스트 삭제
```http
DELETE /api/blog/posts/{postId}
Authorization: Bearer {token}
```

#### 좋아요 증가
```http
POST /api/blog/posts/{postId}/like
```

#### 좋아요 감소
```http
POST /api/blog/posts/{postId}/unlike
```

### 이미지 API

#### 이미지 업로드 (서버 업로드)
```http
POST /api/images/upload
Authorization: Bearer {token}
Content-Type: multipart/form-data

file: [이미지 파일]
```

#### Pre-signed URL 생성 (클라이언트 직접 업로드)
```http
POST /api/images/presigned-url
Authorization: Bearer {token}
Content-Type: application/x-www-form-urlencoded

fileName=example.jpg&contentType=image/jpeg
```

#### 이미지 정보 조회
```http
GET /api/images/{imageId}
```

#### 임시 다운로드 URL 생성
```http
GET /api/images/{imageId}/download-url?expirationMinutes=60
```

#### 사용자 이미지 목록
```http
GET /api/images/user/{userId}?page=0&size=20
```

#### 이미지 삭제
```http
DELETE /api/images/{imageId}
Authorization: Bearer {token}
```

## 🚀 실행 방법

1. **의존성 설치**
   ```bash
   ./gradlew build
   ```

2. **환경변수 로드**
   ```bash
   source .env
   ```

3. **애플리케이션 실행**
   ```bash
   ./gradlew bootRun
   ```

4. **Swagger UI 접속**
   - URL: `http://localhost:8080/swagger-ui.html`
   - API 그룹별로 문서화되어 있습니다:
     - 인증 API
     - 팔로우 API
     - 블로그 API
     - 이미지 API
     - 공개 API

## 🔍 주요 특징

### 성능 최적화
- **비동기 파일 업로드**: 5MB 이상 파일은 자동으로 비동기 처리
- **Pre-signed URL**: 클라이언트가 직접 S3에 업로드하여 서버 부하 감소
- **팔로우 수 캐싱**: 빠른 조회를 위한 캐시 컬럼
- **좋아요/조회수 원자적 연산**: 동시성 처리
- **페이징 처리**: 대용량 데이터 효율적 관리
- **스레드 풀 최적화**: 파일 업로드 전용 스레드 풀

### 보안
- **JWT 기반 인증**
- **리소스 접근 권한 검증**
- **파일 업로드 검증**: 크기(50MB), 형식(JPEG, PNG, GIF, WebP)
- **S3 서버사이드 암호화**: AES256
- **Pre-signed URL 만료**: 15분 기본 설정

### 확장성
- **도메인별 패키지 구조**
- **RESTful API 설계**
- **포괄적인 예외 처리**
- **비동기 작업 지원**
- **AWS S3 연동**

## 🛠️ 성능 튜닝 옵션

### 1. 파일 업로드 최적화
```yaml
# application.yaml
spring:
  servlet:
    multipart:
      max-file-size: 50MB        # 최대 파일 크기
      max-request-size: 50MB     # 최대 요청 크기
```

### 2. 스레드 풀 설정
```java
// AsyncConfig.java
@Bean(name = "fileUploadExecutor")
public Executor fileUploadExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(3);      // 코어 스레드 수
    executor.setMaxPoolSize(10);      // 최대 스레드 수
    executor.setQueueCapacity(100);   // 큐 용량
    return executor;
}
```

### 3. S3 설정 최적화
```java
// AwsS3Service.java
.storageClass(StorageClass.STANDARD_IA)    // 비용 최적화
.serverSideEncryption(ServerSideEncryption.AES256)  // 보안
```

## 📊 모니터링

### 로그 레벨 설정
```yaml
logging:
  level:
    com.apple.appleplayground: DEBUG
    software.amazon.awssdk: INFO
```

### 주요 로그 메시지
- `Async upload completed`: 비동기 업로드 완료
- `Generated presigned upload URL`: Pre-signed URL 생성
- `File uploaded to S3`: S3 업로드 성공
- `Follow relationship created`: 팔로우 관계 생성

## 📖 추가 문서

상세한 API 문서는 애플리케이션 실행 후 Swagger UI에서 확인할 수 있습니다.

- 각 API별 요청/응답 스키마
- 에러 코드 및 메시지
- 실제 API 테스트 기능
- Pre-signed URL 사용법
- 비동기 업로드 가이드

## 🔗 유용한 링크

- [AWS S3 Console](https://s3.console.aws.amazon.com/)
- [AWS IAM Console](https://console.aws.amazon.com/iam/)
- [Spring Boot Async Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#scheduling)
- [AWS SDK for Java Documentation](https://docs.aws.amazon.com/sdk-for-java/)
