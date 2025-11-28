# CLAUDE.md

이 파일은 Claude Code (claude.ai/code)가 이 저장소에서 작업할 때 참조하는 가이드입니다.

## 프로젝트 개요

Wiiee는 방탈출 검색 및 소셜 플랫폼으로, 세 가지 주요 모듈로 구성됩니다:
- **API**: 모바일/웹 클라이언트용 Public REST API (port 3781)
- **Admin**: Thymeleaf UI를 사용하는 내부 관리 대시보드 (port 8080)
- **Push**: Firebase Cloud Messaging 푸시 알림 서비스 (port 8082)
- **Common**: 공유 도메인 엔티티, 리포지토리, 유틸리티

기술 스택: Spring Boot 2.5.2, JPA/Hibernate, PostgreSQL, QueryDSL, JWT 인증

## 빌드 & 실행 명령어

### 전체 프로젝트 빌드
```bash
./gradlew build
```

### 특정 모듈 실행
```bash
# API 서버 (public REST API)
./gradlew :api:bootRun

# Admin 서버 (관리 대시보드)
./gradlew :admin:bootRun

# Push 서버 (Firebase 알림)
./gradlew :push:bootRun
```

### 특정 프로파일로 실행
```bash
# 사용 가능한 프로파일: local, dev, test, prod
./gradlew :api:bootRun --args='--spring.profiles.active=dev'
```

### 테스트
```bash
# 모든 테스트 실행
./gradlew test

# 특정 모듈 테스트
./gradlew :api:test
./gradlew :common:test

# 단일 테스트 클래스 실행
./gradlew :api:test --tests "com.wiiee.server.api.acceptance.content.ContentAcceptanceTest"

# 단일 테스트 메서드 실행
./gradlew :api:test --tests "com.wiiee.server.api.acceptance.content.ContentAcceptanceTest.testGetContentDetail"
```

### Clean 후 재빌드
```bash
./gradlew clean build
```

### QueryDSL Q 클래스 생성
QueryDSL Q 클래스는 빌드 시 자동 생성됩니다. 재생성이 필요한 경우:
```bash
./gradlew clean compileJava
```

## 아키텍처

### 모듈 의존성
- **api** → **common**에 의존 (도메인 엔티티, 리포지토리)
- **admin** → **common**에 의존 (도메인 엔티티, 리포지토리)
- **push** → **common**에 의존 (도메인 엔티티)
- **common** → 독립 모듈 (bootJar 비활성화, 공유 코드 제공)

### API 모듈 구조 (`api/`)
```
api/
├── application/          # Controller와 DTO (프레젠테이션 계층)
│   ├── *RestController   # REST 엔드포인트
│   ├── page/             # 페이지별 모델 (MainPageModel, ContentDetailPageModel)
│   ├── response/         # ApiResponse 래퍼
│   └── security/         # JWT 필터, 인증
├── domain/               # 비즈니스 로직 서비스
│   ├── *Service          # 도메인 서비스
│   └── *Repository       # 커스텀 QueryDSL 리포지토리 (common 리포지토리 확장)
├── infrastructure/       # 외부 통합
│   └── slack/            # Slack 알림
└── config/               # 설정 클래스
```

### Admin 모듈 구조 (`admin/`)
```
admin/
├── controller/           # Thymeleaf 컨트롤러
├── service/              # 비즈니스 로직
├── repository/           # 데이터 접근 (common 확장)
├── form/                 # Admin UI용 Form DTO
└── resources/
    ├── templates/        # Thymeleaf HTML 템플릿
    └── static/           # JS, CSS, 벤더 라이브러리
```

### Common 모듈 구조 (`common/`)
```
common/
└── domain/
    ├── BaseEntity        # Soft delete 지원 (deleted, deletedAt)
    ├── DefaultEntity     # 타임스탬프 (createdAt, updatedAt)
    ├── user/             # User 엔티티 및 리포지토리
    ├── company/          # Company (방탈출 업체)
    ├── content/          # 테마/방 컨텐츠
    ├── gathering/        # 그룹 모집 게시글 및 멤버
    ├── recommendation/   # 추천 데이터
    └── wbti/             # WBTI 성격 유형
```

### 주요 아키텍처 패턴

**엔티티 상속**:
- 모든 엔티티는 `BaseEntity`를 상속 (soft delete 제공: `deleted`, `deletedAt`)
- `BaseEntity`는 `DefaultEntity`를 상속 (감사 타임스탬프 제공: `createdAt`, `updatedAt`)
- JPA auditing은 `@EnableJpaAuditing`을 통해 활성화

**엔티티 스캔**:
- 모든 모듈은 `@EntityScan("com.wiiee.server.common.domain")`을 사용하여 common 모듈의 공유 엔티티 로드

**리포지토리 패턴**:
- 기본 리포지토리는 `common/domain/*Repository`에 정의
- 모듈별 리포지토리는 common 리포지토리를 확장하고 QueryDSL 쿼리 추가
- 예시: `api/domain/recommendation/RecommendationRepository`는 common 리포지토리 확장

**서비스 계층**:
- `api/domain/*Service`의 도메인 서비스에 비즈니스 로직 포함
- 컨트롤러는 서비스에 위임하고 `ApiResponse<T>` 래퍼 반환

**보안**:
- `SecurityAuthenticationFilter`를 통한 JWT 기반 인증
- 모든 요청에 대한 토큰 검증 (공개 엔드포인트 제외)
- `@AuthUser` 어노테이션이 인증된 사용자 컨텍스트 주입

**Soft Delete**:
- 모든 엔티티는 `deleted` boolean과 `deletedAt` 타임스탬프를 통한 soft delete 지원
- 삭제된 레코드를 조회하지 않는 한 쿼리는 반드시 `deleted = false` 필터링 필요

### 데이터베이스
- 모든 환경에서 PostgreSQL 사용 (dev, test, prod)
- 연결 정보는 `application-{profile}.yml`에 설정
- Hibernate DDL: `update` 모드 (테이블 자동 생성)
- local/dev 프로파일에서 쿼리 로깅 활성화

### 외부 통합
- **AWS S3**: 이미지 업로드 (bucket: wiiee-test, region: ap-northeast-2)
- **Firebase**: push 모듈을 통한 푸시 알림
- **Slack**: 리뷰 모니터링 알림 (channel: #미승인된_유저리뷰_notice_dev)

## 개발 워크플로우

### 새 기능 추가하기
1. `common/src/main/java/com/wiiee/server/common/domain/{feature}/`에 엔티티 정의
2. `JpaRepository`를 확장하는 리포지토리 인터페이스 생성
3. 빌드하여 QueryDSL Q 클래스 생성: `./gradlew :common:build`
4. `api/src/main/java/com/wiiee/server/api/domain/{feature}/`에 서비스 구현
5. `api/src/main/java/com/wiiee/server/api/application/{feature}/`에 REST 컨트롤러 생성
6. `api/src/test/java/.../acceptance/{feature}/`에 acceptance 테스트 추가

### QueryDSL 작업하기
- Q 클래스는 `build/generated/sources/annotationProcessor/`에 자동 생성
- 복잡한 쿼리는 `QPredicate` 사용
- 리포지토리는 predicate 지원을 위해 `QuerydslPredicateExecutor<Entity>` 확장
- 생성된 소스 정리: `./gradlew clean`으로 삭제 및 재생성

### 테스트 전략
- **단위 테스트**: `common/src/test/`의 도메인 엔티티 테스트
- **Acceptance 테스트**: `api/src/test/.../acceptance/`의 End-to-end REST API 테스트
- **테스트 프레임워크**: JUnit 5, API 테스트용 RestAssured, 테스트 데이터용 FixtureMonkey
- **테스트 프로파일**: PostgreSQL 또는 별도 테스트 데이터베이스 사용 (`application-test.yml` 확인)
- **Testcontainers**: Acceptance 테스트에서 PostgreSQL 컨테이너 사용
  - `api/src/test/resources/.testcontainers.properties`에서 Docker 설정 관리
  - IntelliJ와 Gradle 모두에서 동일하게 동작

## 주의사항 (Common Gotchas)

### 프로파일 설정
- 기본 프로파일은 `local` (`application.yml`에 설정)
- 각 모듈은 프로파일별 설정 파일 보유: `application-{profile}.yml`
- **보안**: local 설정 파일의 credential은 커밋하지 말 것 (prod는 환경 변수 사용)

### QueryDSL 생성
- Q 클래스가 없으면 `./gradlew clean compileJava` 실행
- 생성된 클래스 위치: `build/generated/sources/annotationProcessor/java/main/`
- 이전 생성 소스 제거: `clean { delete file('src/main/generated') }` 추가됨

### JPA Auditing
- 메인 애플리케이션 클래스에 `@EnableJpaAuditing` 필요
- `createdAt`과 `updatedAt`은 Spring Data JPA에 의해 자동 입력
- 엔티티가 `DefaultEntity` 또는 `BaseEntity`를 상속하는지 확인

### Soft Delete 패턴
- 엔티티를 hard delete 하지 말고 `deleted = true`와 `deletedAt = now()` 설정
- 모든 쿼리는 삭제된 레코드 필터링 필요: `WHERE deleted = false`
- Admin UI에 "삭제된 항목 복원" 기능 필요할 수 있음

### 모듈 의존성
- `common` 변경 시 의존하는 모듈(`api`, `admin`, `push`) 재빌드 필요
- `./gradlew :common:build` 후 `./gradlew :api:build` 실행

### API 응답 형식
모든 REST 엔드포인트는 표준화된 형식으로 응답:
```json
{
  "code": 200,
  "message": null,
  "data": { ... },
  "responseTime": "2025-11-19 02:30:45"
}
```
컨트롤러는 반드시 `ApiResponse.success(data)` 또는 `ApiResponse.error()` 사용

### 포트 충돌
- API: 3781
- Admin: 8080
- Push: 8082
- 서비스 시작 전 포트가 사용 가능한지 확인: `lsof -i :{port}`

### Testcontainers 설정

#### 설정 파일 위치
- **프로젝트**: `testcontainers.properties` (커밋 가능, 프로젝트 공통 설정)
- **글로벌**: `~/.testcontainers.properties` (커밋 안 함, 개인 머신 설정)

#### 글로벌 설정 예시 (`~/.testcontainers.properties`)
```properties
docker.client.strategy=org.testcontainers.dockerclient.UnixSocketClientProviderStrategy
docker.host=unix:///Users/[username]/.docker/run/docker.sock  # macOS Docker Desktop
```

#### AcceptanceTest 패턴 (중요!)

**❌ 잘못된 방법 - 구체 클래스에서 @Container 사용:**
```java
// 각 테스트 클래스에 직접 @Testcontainers 선언
@Testcontainers
public class CommentAcceptanceTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(...);
}
```
**문제점**:
- 각 테스트 클래스마다 새로운 컨테이너가 다른 포트로 시작됨
- 하지만 Spring Context는 첫 번째 컨테이너의 포트로 초기화되어 재사용됨
- Spring은 `@SpringBootTest` 설정이 같으면 Context를 캐시하고 재사용함
- `@DynamicPropertySource`는 Context 생성 시에만 호출되므로 새 포트 정보가 반영 안됨
- 두 번째 테스트 클래스부터는 새 포트의 컨테이너에 연결할 수 없어 `java.net.ConnectException` 발생

**예시:**
```
1. CommentAcceptanceTest 실행 → 컨테이너 포트 55001 시작
   → @DynamicPropertySource 호출 → Spring Context 생성 (포트 55001로 초기화)
2. UserAcceptanceTest 실행 → 새 컨테이너 포트 55002 시작
   → Spring Context 재사용 (포트 55001 그대로)
   → DataSource가 55001로 연결 시도 → Connection refused!
```

**✅ 올바른 방법 - static 초기화 블록 (권장):**
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest {

    static PostgreSQLContainer<?> postgres;

    static {  // static 초기화 블록 사용
        postgres = new PostgreSQLContainer<>("postgres:12-alpine")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withUrlParam("sslmode", "disable");
        postgres.start();  // 수동 시작
    }

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        // ...
    }
}
```
**장점**:
- JVM 전체에서 단 한 번만 컨테이너 생성 및 시작
- 모든 테스트 클래스가 동일한 포트의 동일한 컨테이너 사용
- IntelliJ, Gradle 등 모든 환경에서 일관된 동작 보장
- 컨테이너 생명주기를 완전히 제어 가능
- `DatabaseCleanup`으로 각 테스트 전에 데이터 정리하여 격리 보장
- 테스트 속도 향상 (컨테이너 재시작 불필요)

**참고: @Testcontainers + @Container 방식**
```java
@Testcontainers  // 추상 클래스에 선언
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(...);
}
```
- 이론적으로는 동작해야 하지만 실제로는 IntelliJ와 Gradle에서 동작이 다를 수 있음
- 일부 환경에서 여전히 포트 불일치 문제 발생 가능
- static 초기화 블록 방식이 더 안정적

#### 환경 독립성
- Docker socket 경로는 `~/.testcontainers.properties`에만 설정
- 프로젝트 코드에는 하드코딩하지 않음
- 각 개발자가 자신의 환경에 맞게 글로벌 설정 파일 생성

#### 문제 해결
- **컨테이너 시작 실패**: `~/.testcontainers.properties`에 `docker.host` 설정 확인
- **전체 테스트 실패**: static 초기화 블록 패턴 사용 확인
- **포트 충돌**: 오래된 컨테이너 정리 `docker ps -a --filter ancestor=postgres:12-alpine --format "{{.ID}}" | xargs docker rm -f`
