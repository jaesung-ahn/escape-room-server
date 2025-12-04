# 인수 테스트(Acceptance Test) 문제 해결 가이드

이 문서는 Wiiee 프로젝트에서 인수 테스트 작성 및 실행 중 발생한 문제와 해결 방법을 정리한 문서입니다.

---

## 목차
1. [NullPointerException 문제](#1-nullpointerexception-문제)
2. [디버깅 코드 정리](#2-디버깅-코드-정리)
3. [테스트 실행 방법](#3-테스트-실행-방법)
4. [일반적인 문제 해결](#4-일반적인-문제-해결)

---

## 1. NullPointerException 문제

### 문제 증상

유저 조회 API 테스트(`UserAcceptanceTest.getUser()`)가 다음과 같은 에러로 실패:

```
Status Code: 400
Response Body: {"code":400,"message":"처리 중 오류가 발생했습니다.","errorDetails":["/api/user/1"]}
```

### 원인 분석

`UserModel.from()` 메서드에서 선택 사항인 필드들에 대한 null 체크가 없어 `NullPointerException` 발생:

```java
// 문제가 있던 코드
.userGender(profile.getUserGenderType().getName())  // userGenderType이 null이면 NPE
.zamfitTest(profile.getWbti().getName())            // wbti가 null이면 NPE
```

**왜 null인가?**
- 회원가입 시 `userGenderType`은 선택 사항
- `wbti` (WBTI 성격 유형)는 나중에 설정하는 필드
- 일반 회원가입 직후에는 두 필드 모두 null일 수 있음

### 해결 방법

#### 파일: `api/src/main/java/com/wiiee/server/api/application/user/UserModel.java`

```java
public static UserModel from(User user) {
    Profile profile = user.getProfile();

    City city = profile.getCity();
    String cityName = city != null ? city.getName() : null;
    String stateName = city != null ? State.valueOf(city.getParentCode()).getName() : null;
    String ageGroup = profile.getBirthDate() != null ? LocalDateTimeUtil.getAgeGroup(profile.getBirthDate()) : null;

    // ✅ null 체크 추가
    UserGenderType userGenderType = profile.getUserGenderType();
    String userGenderName = userGenderType != null ? userGenderType.getName() : null;
    String zamfitTestName = profile.getWbti() != null ? profile.getWbti().getName() : null;

    return UserModel.builder()
            .id(user.getId())
            .email(user.getEmail())
            .nickname(profile.getNickname())
            .state(stateName)
            .city(cityName)
            .userGender(userGenderName)      // null-safe
            .ageGroup(ageGroup)
            .zamfitTest(zamfitTestName)      // null-safe
            .build();
}
```

### 일반적인 패턴

DTO 변환 시 선택 사항 필드에 대한 null 체크:

```java
// ❌ 위험한 방식
.field(entity.getOptionalField().getValue())

// ✅ 안전한 방식
OptionalType optional = entity.getOptionalField();
String value = optional != null ? optional.getValue() : null;
.field(value)

// ✅ 더 간결한 방식 (Java 8+)
.field(Optional.ofNullable(entity.getOptionalField())
    .map(OptionalType::getValue)
    .orElse(null))
```

### 테스트로 검증

```java
@Test
@DisplayName("유저 조회 - 선택 필드가 null인 경우")
void getUser_withNullOptionalFields() {
    // given: userGenderType, wbti 없이 회원가입
    String nickname = "테스트유저";
    ExtractableResponse<Response> signupResponse =
        회원가입_요청("test@example.com", nickname, "password123!");

    Integer userId = signupResponse.path("data.id");

    // when: 유저 조회
    ExtractableResponse<Response> response = 유저_조회_요청(userId);

    // then: null 필드는 null로 반환되어야 함
    response.response()
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("data.userGender", nullValue())
        .body("data.zamfitTest", nullValue());
}
```

---

## 2. 디버깅 코드 정리

### 문제 증상

프로덕션 코드에 다음과 같은 디버깅 코드가 남아있음:
- `System.out.println()` 또는 `System.err.println()`
- `e.printStackTrace()`
- 불필요한 변수나 메서드 호출 (예: `List<User> all = userRepository.findAll();`)

### 문제점

1. **성능 문제**: `System.out`은 동기화되어 있어 성능 저하 발생
2. **보안 문제**: 민감한 정보가 콘솔에 노출될 수 있음
3. **로그 관리 어려움**: 로깅 프레임워크를 통하지 않아 중앙 관리 불가
4. **프로덕션 환경 문제**: 에러 추적 및 모니터링 어려움

### 해결 방법

#### 2.1. System.out.println() 제거

##### 검색 방법
```bash
# API 모듈에서 System.out/err 검색
grep -r "System\.(out|err)\.print" api/src/main/java/

# 전체 프로젝트에서 검색
find . -name "*.java" -type f -exec grep -l "System\.out\.print" {} \;
```

##### 제거 예시

**Before:**
```java
public void applyGathering(long gatheringId, long userId) {
    int memberSize = gathering.getGatheringMembers().size();
    System.out.println("memberSize = " + memberSize);  // ❌ 제거
    if (memberSize == gathering.getGatheringInfo().getMaxPeople()) {
        throw new CustomException(...);
    }
}
```

**After:**
```java
public void applyGathering(long gatheringId, long userId) {
    int memberSize = gathering.getGatheringMembers().size();
    // System.out 제거 - 필요시 log.debug() 사용
    if (memberSize == gathering.getGatheringInfo().getMaxPeople()) {
        throw new CustomException(...);
    }
}
```

#### 2.2. printStackTrace() → log.error() 변경

##### 검색 방법
```bash
# printStackTrace() 사용처 검색
grep -r "\.printStackTrace()" api/src/main/java/
```

##### 변경 예시

**Before:**
```java
try {
    // some operation
} catch (IOException e) {
    log.error(e.getMessage());      // ❌ 메시지만 로깅
    e.printStackTrace();             // ❌ 콘솔 출력
}
```

**After:**
```java
try {
    // some operation
} catch (IOException e) {
    log.error("Failed to perform operation", e);  // ✅ 예외 객체 포함
}
```

**더 나은 방식:**
```java
try {
    // some operation
} catch (IOException e) {
    log.error("Failed to upload file to S3: bucket={}, key={}", bucket, key, e);
    throw new CustomException(StatusCode.FILE_UPLOAD_FAILED, e);
}
```

#### 2.3. 불필요한 디버깅 코드 제거

**Before:**
```java
@Transactional(readOnly = true)
public User findById(Long id) {
    List<User> all = userRepository.findAll();  // ❌ 디버깅용 코드
    return userRepository.findById(id).orElseThrow(NoSuchElementException::new);
}
```

**After:**
```java
@Transactional(readOnly = true)
public User findById(Long id) {
    return userRepository.findById(id).orElseThrow(NoSuchElementException::new);
}
```

### 변경 대상 파일 목록

이번 정리에서 수정한 파일들:

1. `api/src/main/java/com/wiiee/server/api/domain/gathering/GatheringService.java`
   - `System.out.println()` 4개 제거
   - `printStackTrace()` 4개 → `log.error()` 변경

2. `api/src/main/java/com/wiiee/server/api/application/response/ApiResponse.java`
   - `System.out.println()` 3개 제거

3. `api/src/main/java/com/wiiee/server/api/domain/user/UserService.java`
   - 불필요한 `findAll()` 호출 제거

4. `api/src/main/java/com/wiiee/server/api/infrastructure/aws/S3Util.java`
   - `printStackTrace()` 2개 → `log.error()` 변경
   - `log.info()` → `log.error()` 변경 (에러 로그는 error 레벨로)

5. `api/src/main/java/com/wiiee/server/api/application/security/SecurityAuthenticationFilter.java`
   - `printStackTrace()` 1개 → `log.error()` 변경

### 로깅 베스트 프랙티스

```java
@Slf4j  // Lombok 사용
@Service
public class MyService {

    // ✅ 적절한 로그 레벨 사용
    public void processData(Long id) {
        log.debug("Processing data for id: {}", id);  // 개발 중 디버깅

        try {
            Data data = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Data not found: " + id));

            log.info("Successfully processed data: id={}, status={}", id, data.getStatus());

        } catch (NotFoundException e) {
            log.warn("Data not found: id={}", id);  // 예상 가능한 에러
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while processing data: id={}", id, e);  // 예외 포함
            throw new CustomException(StatusCode.INTERNAL_ERROR, e);
        }
    }
}
```

**로그 레벨 가이드:**
- `TRACE`: 매우 상세한 디버깅 정보
- `DEBUG`: 일반적인 디버깅 정보 (개발 환경)
- `INFO`: 중요한 비즈니스 이벤트 (프로덕션 기본)
- `WARN`: 예상 가능한 에러, 경고 상황
- `ERROR`: 예상하지 못한 에러, 즉각적인 조치 필요

---

## 3. 테스트 실행 방법

### 3.1. 단일 테스트 실행

```bash
# 특정 테스트 클래스 실행
./gradlew :api:test --tests "com.wiiee.server.api.acceptance.user.UserAcceptanceTest"

# 특정 테스트 메서드 실행
./gradlew :api:test --tests "com.wiiee.server.api.acceptance.user.UserAcceptanceTest.getUser"

# 패턴 매칭으로 실행
./gradlew :api:test --tests "*UserAcceptanceTest*"
./gradlew :api:test --tests "*AcceptanceTest"  # 모든 인수 테스트
```

### 3.2. 테스트 디버깅

#### 실제 응답 확인하기

```java
@Test
@DisplayName("유저 조회")
void getUser() {
    // given
    String nickname = "조회유저";
    ExtractableResponse<Response> signupResponse =
        회원가입_요청("test@example.com", nickname, "password123!");
    Integer userId = signupResponse.path("data.id");

    // when
    ExtractableResponse<Response> response = 유저_조회_요청(userId);

    // 디버깅: 응답 내용 출력
    System.out.println("=== 응답 상태 코드 ===");
    System.out.println(response.statusCode());
    System.out.println("=== 응답 본문 ===");
    System.out.println(response.asString());

    // then
    유저_조회_성공_확인(response, nickname);
}
```

#### 상세 로그와 함께 실행

```bash
# --info 옵션으로 상세 로그 확인
./gradlew :api:test --tests "*UserAcceptanceTest.getUser" --info 2>&1 | grep -E "(===|Status|Response|Error)"

# 전체 로그 파일로 저장
./gradlew :api:test --tests "*UserAcceptanceTest.getUser" --info > test-output.log 2>&1
```

### 3.3. 테스트 리포트 확인

테스트 실패 시 HTML 리포트가 자동 생성됨:
```
api/build/reports/tests/test/index.html
```

브라우저로 열어서 상세 실패 내역 확인 가능.

---

## 4. 일반적인 문제 해결

### 4.1. 테스트 실패 시 체크리스트

1. **데이터베이스 상태 확인**
   - Testcontainers가 정상 시작되었는가?
   - 데이터베이스 연결 정보가 올바른가?

2. **테스트 격리 확인**
   - 각 테스트가 독립적으로 실행되는가?
   - `DatabaseCleanup` 같은 정리 로직이 작동하는가?

3. **API 응답 확인**
   - 실제 반환된 상태 코드는?
   - 에러 메시지는 무엇인가?
   - 예상한 데이터가 반환되는가?

4. **로그 확인**
   - 서버 측 에러 로그는?
   - 스택 트레이스는?

### 4.2. NullPointerException 디버깅

```java
// NPE 발생 시 어느 필드가 null인지 확인
@Test
void debugNullPointerException() {
    ExtractableResponse<Response> response = 유저_조회_요청(userId);

    // 응답 본문을 Map으로 파싱
    Map<String, Object> responseBody = response.as(Map.class);
    Map<String, Object> data = (Map<String, Object>) responseBody.get("data");

    // 각 필드 확인
    System.out.println("id: " + data.get("id"));
    System.out.println("email: " + data.get("email"));
    System.out.println("nickname: " + data.get("nickname"));
    System.out.println("userGender: " + data.get("userGender"));  // null일 수 있음
    System.out.println("zamfitTest: " + data.get("zamfitTest"));  // null일 수 있음
}
```

### 4.3. 400 Bad Request 디버깅

```java
@Test
void debug400Error() {
    ExtractableResponse<Response> response = 유저_조회_요청(userId);

    if (response.statusCode() == 400) {
        System.out.println("=== 400 에러 상세 정보 ===");
        System.out.println("code: " + response.path("code"));
        System.out.println("message: " + response.path("message"));
        System.out.println("errorDetails: " + response.path("errorDetails"));
        System.out.println("전체 응답: " + response.asString());
    }
}
```

### 4.4. 테스트 데이터 문제

```java
@Test
void verifyTestData() {
    // 회원가입 응답 확인
    ExtractableResponse<Response> signupResponse =
        회원가입_요청("test@example.com", "테스터", "password123!");

    System.out.println("=== 회원가입 응답 ===");
    System.out.println("전체: " + signupResponse.asString());

    // 각 필드 확인
    assertThat(signupResponse.path("data.id")).isNotNull();
    assertThat(signupResponse.path("data.email")).isEqualTo("test@example.com");
    assertThat(signupResponse.path("data.nickname")).isEqualTo("테스터");
    assertThat(signupResponse.path("data.accessToken")).isNotNull();
}
```

### 4.5. Security 설정 문제

```java
// SecurityConfig에서 해당 엔드포인트가 permitAll인지 확인
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers(GET, "/api/user/*").permitAll()  // ✅ 확인
            .requestMatchers(POST, "/api/user", "/api/user/login/**").permitAll()
            .anyRequest().authenticated()
        );
        return http.build();
    }
}
```

403 Forbidden 에러가 발생하면 Security 설정 확인 필요.

### 4.6. 타입 변환 문제

```java
// Integer vs Long 타입 주의
Integer userId = signupResponse.path("data.id");  // ✅ JSON에서 Integer로 파싱됨

// 컨트롤러는 Long 타입 받음
@GetMapping("/{id}")
public ApiResponse<UserModel> getUser(@PathVariable("id") Long id) {  // Long 타입
    // Integer → Long 자동 변환됨
}
```

---

## 5. 참고 자료

### 5.1. 관련 문서
- [CLAUDE.md](../CLAUDE.md) - 프로젝트 전체 가이드
- [ENTITY_DEPENDENCIES.md](./ENTITY_DEPENDENCIES.md) - 엔티티 의존성 문서

### 5.2. 테스트 패턴
- **Given-When-Then**: 테스트 구조화 패턴
- **AAA (Arrange-Act-Assert)**: 테스트 작성 패턴
- **Test Fixture**: 테스트 데이터 준비 패턴

### 5.3. 유용한 명령어

```bash
# 전체 테스트 실행
./gradlew test

# API 모듈 테스트만 실행
./gradlew :api:test

# 테스트 결과 정리
./gradlew clean test

# 캐시 정리 후 테스트
./gradlew clean build --no-build-cache

# 특정 패키지의 테스트 실행
./gradlew :api:test --tests "com.wiiee.server.api.acceptance.*"
```

---

## 6. 체크리스트

인수 테스트 작성 시 체크리스트:

### 작성 전
- [ ] 테스트할 API 엔드포인트 확인
- [ ] 필요한 테스트 데이터 준비
- [ ] Security 설정 확인 (인증 필요 여부)

### 작성 중
- [ ] Given-When-Then 구조로 작성
- [ ] 의미 있는 테스트 메서드명 작성
- [ ] 헬퍼 메서드 재사용
- [ ] 검증 로직 명확히 작성

### 작성 후
- [ ] 테스트 단독 실행 확인
- [ ] 전체 테스트와 함께 실행 확인
- [ ] 디버깅 코드 제거
- [ ] 커밋 전 최종 테스트

### 코드 정리
- [ ] `System.out.println()` 제거
- [ ] `printStackTrace()` → `log.error()` 변경
- [ ] 불필요한 디버깅 코드 제거
- [ ] 주석 처리된 코드 제거

---

## 변경 이력

- **2025-12-04**: 초기 문서 작성
  - UserModel NPE 문제 해결
  - 디버깅 코드 정리 가이드 추가
  - 테스트 실행 방법 정리