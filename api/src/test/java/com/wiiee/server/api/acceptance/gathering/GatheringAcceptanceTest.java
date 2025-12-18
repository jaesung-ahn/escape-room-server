package com.wiiee.server.api.acceptance.gathering;

import com.wiiee.server.api.AcceptanceTest;
import com.wiiee.server.api.domain.admin.AdminRepository;
import com.wiiee.server.common.domain.admin.AdminUser;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;

@DisplayName("Gathering API 인수 테스트")
class GatheringAcceptanceTest extends AcceptanceTest {

    @Autowired
    private AdminRepository adminRepository;

    private Long testCompanyId;
    private Long testContentId;
    private String accessToken;
    private Long testUserId;

    @BeforeEach
    void setUpData() {
        // 1. AdminUser 생성
        AdminUser admin = AdminUser.of("gathering_test_admin@wiiee.com", "admin123!");
        Long adminId = adminRepository.save(admin).getId();

        // 2. Company 생성
        testCompanyId = 업체_생성_후_ID_반환(adminId, "동행 테스트 방탈출");

        // 3. User 생성 및 토큰 발급
        Map<String, Object> userResponse = 회원가입_후_응답_반환("gathering_user@example.com", "동행테스터", "pass123!");
        accessToken = (String) userResponse.get("accessToken");
        testUserId = ((Number) userResponse.get("userId")).longValue();

        // 4. Content 생성
        testContentId = 컨텐츠_생성_후_ID_반환("동행 테스트용 방탈출");
    }

    // ===== 테스트 케이스 =====

    @Test
    @DisplayName("동행 모집 등록")
    void createGathering() {
        // given: 동행 모집 정보
        String title = "함께 방탈출 하실 분!";
        String information = "초보자 환영합니다";

        // when: 동행 모집 등록 요청
        ExtractableResponse<Response> response = 동행_모집_등록_요청(testContentId, title, information);

        // then: 성공 응답
        동행_모집_등록_성공_확인(response, title, information);
    }

    @Test
    @DisplayName("동행 모집 상세 조회")
    void getGathering() {
        // given: 기존 동행 모집 생성
        String title = "방탈출 동행 모집";
        String information = "재미있게 즐겨요";
        Long gatheringId = 동행_모집_등록_후_ID_반환(testContentId, title, information);

        // when: 동행 모집 조회
        ExtractableResponse<Response> response = 동행_모집_조회_요청(gatheringId);

        // then: 성공 응답
        동행_모집_조회_성공_확인(response, gatheringId, title, information);
    }

    @Test
    @DisplayName("동행 모집 리스트 조회")
    void getGatherings() {
        // given: 여러 동행 모집 생성
        동행_모집_등록_후_ID_반환(testContentId, "첫 번째 동행", "초보 환영");
        동행_모집_등록_후_ID_반환(testContentId, "두 번째 동행", "고수 환영");
        동행_모집_등록_후_ID_반환(testContentId, "세 번째 동행", "누구나 환영");

        // when: 동행 모집 리스트 조회
        ExtractableResponse<Response> response = 동행_모집_리스트_조회_요청();

        // then: 성공 응답 (최소 3개 이상)
        동행_모집_리스트_조회_성공_확인(response, 3);
    }

    @Test
    @DisplayName("동행 모집 수정")
    void updateGathering() {
        // given: 기존 동행 모집 생성
        String originalTitle = "처음 작성한 제목";
        String originalInfo = "처음 작성한 내용";
        Long gatheringId = 동행_모집_등록_후_ID_반환(testContentId, originalTitle, originalInfo);

        // when: 동행 모집 수정 요청
        String updatedTitle = "수정된 제목";
        String updatedInfo = "수정된 내용";
        ExtractableResponse<Response> response = 동행_모집_수정_요청(gatheringId, updatedTitle, updatedInfo);

        // then: 성공 응답
        동행_모집_수정_성공_확인(response);
    }

    @Test
    @DisplayName("동행 모집 삭제")
    void deleteGathering() {
        // given: 기존 동행 모집 생성
        Long gatheringId = 동행_모집_등록_후_ID_반환(testContentId, "삭제할 동행", "삭제 테스트");

        // when: 동행 모집 삭제 요청
        ExtractableResponse<Response> response = 동행_모집_삭제_요청(gatheringId);

        // then: 성공 응답
        동행_모집_삭제_성공_확인(response);
    }

    @Test
    @DisplayName("동행 참가 신청")
    void applyGathering() {
        // given: 다른 사용자의 동행 모집 생성
        Map<String, Object> anotherUserResponse = 회원가입_후_응답_반환("leader@example.com", "방장", "pass123!");
        String leaderToken = (String) anotherUserResponse.get("accessToken");

        Long gatheringId = 동행_모집_등록_후_ID_반환_with_token(testContentId, "동행 모집합니다", "참가해주세요", leaderToken);

        // when: 참가 신청 요청
        String requestReason = "참가하고 싶습니다!";
        ExtractableResponse<Response> response = 동행_참가_신청_요청(gatheringId, requestReason);

        // then: 성공 응답
        동행_참가_신청_성공_확인(response);
    }

    @Test
    @DisplayName("동행 찜 등록")
    void addGatheringFavorite() {
        // given: 동행 모집 생성
        Long gatheringId = 동행_모집_등록_후_ID_반환(testContentId, "찜 테스트 동행", "찜하기");

        // when: 찜 등록 요청
        ExtractableResponse<Response> response = 동행_찜_등록_요청(gatheringId);

        // then: 성공 응답
        동행_찜_등록_성공_확인(response, gatheringId);
    }

    @Test
    @DisplayName("동행 찜 삭제")
    void deleteGatheringFavorite() {
        // given: 동행 모집 생성 및 찜 등록
        Long gatheringId = 동행_모집_등록_후_ID_반환(testContentId, "찜 삭제 테스트", "찜 후 삭제");
        동행_찜_등록_요청(gatheringId);

        // when: 찜 삭제 요청
        ExtractableResponse<Response> response = 동행_찜_삭제_요청(gatheringId);

        // then: 성공 응답
        동행_찜_삭제_성공_확인(response);
    }

    @Test
    @DisplayName("내 동행 목록 조회")
    void getMyGatherings() {
        // given: 여러 동행 모집 생성
        Long firstId = 동행_모집_등록_후_ID_반환(testContentId, "내 첫 동행", "첫 번째");
        Long secondId = 동행_모집_등록_후_ID_반환(testContentId, "내 둘째 동행", "두 번째");

        // 동행 모집이 제대로 생성되었는지 확인
        assert firstId != null : "First gathering creation failed";
        assert secondId != null : "Second gathering creation failed";

        // when: 내 동행 목록 조회
        ExtractableResponse<Response> response = 내_동행_목록_조회_요청();

        // then: 성공 응답
        내_동행_목록_조회_성공_확인(response);
    }

    @Test
    @DisplayName("동행 모집 등록 - 인증 없이 요청 시 실패")
    void createGathering_unauthorized() {
        // given: 동행 모집 정보
        Map<String, Object> request = 동행_모집_등록_요청_바디("인증 테스트", "인증 실패 테스트");

        // when: 인증 없이 동행 모집 등록
        ExtractableResponse<Response> response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/gathering")
                .then()
                .extract();

        // then: 401 Unauthorized 또는 403 Forbidden
        response.response()
                .then()
                .statusCode(anyOf(equalTo(HttpStatus.UNAUTHORIZED.value()), equalTo(HttpStatus.FORBIDDEN.value())));
    }

    // ===== 헬퍼 메서드 (HTTP 요청) =====

    /**
     * 회원가입 후 응답 반환 (토큰과 userId 포함)
     */
    private Map<String, Object> 회원가입_후_응답_반환(String email, String nickname, String password) {
        Map<String, Object> request = new HashMap<>();
        request.put("email", email);
        request.put("nickname", nickname);
        request.put("password", password);

        ExtractableResponse<Response> response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/user")
                .then()
                .extract();

        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", response.path("data.accessToken"));
        result.put("userId", response.path("data.id"));
        return result;
    }

    /**
     * 업체 생성 후 ID 반환
     */
    private Long 업체_생성_후_ID_반환(Long adminId, String name) {
        Map<String, Object> request = new HashMap<>();
        request.put("adminId", adminId);
        request.put("name", name);
        request.put("stateCode", 1);
        request.put("cityCode", 1);
        request.put("address", "테스트 주소");
        request.put("detailAddress", "상세 주소");
        request.put("notice", "영업 중");
        request.put("contact", "02-1234-5678");
        request.put("url", "https://test.com");
        request.put("isOperated", true);
        request.put("businessDayCodes", List.of(1, 2, 3, 4, 5, 6, 7));
        request.put("isAlwaysOperated", true);
        request.put("imageIds", List.of());
        request.put("registrationImageId", 0L);
        request.put("businessNumber", "123-45-67890");
        request.put("representativeName", "대표자");
        request.put("repContractNumber", "010-1111-2222");
        request.put("chargeContractNumber", "010-3333-4444");
        request.put("bankCode", 1);
        request.put("account", "1234567890");

        Number companyId = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/company")
                .then()
                .extract()
                .path("data.companyId");

        return companyId != null ? companyId.longValue() : null;
    }

    /**
     * 컨텐츠 생성 후 ID 반환
     */
    private Long 컨텐츠_생성_후_ID_반환(String name) {
        Map<String, Object> request = new HashMap<>();
        request.put("companyId", testCompanyId);
        request.put("name", name);
        request.put("genreCode", 1);
        request.put("imageIds", List.of());
        request.put("minPeople", 2);
        request.put("maxPeople", 6);
        request.put("information", "재미있는 방탈출 게임");
        request.put("playTime", 60);
        request.put("activityLevelCode", 2);
        request.put("escapeTypeCode", 1);
        request.put("isCaution", false);
        request.put("difficultyCode", 3);
        request.put("isNoEscapeType", false);
        request.put("isNew", true);
        request.put("newDisplayExpirationDate", LocalDate.now().plusMonths(1).toString());
        request.put("isOperated", true);
        request.put("priceList", List.of());

        Number contentId = RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/content")
                .then()
                .extract()
                .path("data.id");

        return contentId != null ? contentId.longValue() : null;
    }

    /**
     * 동행 모집 등록 요청 바디 생성
     */
    private Map<String, Object> 동행_모집_등록_요청_바디(String title, String information) {
        Map<String, Object> request = new HashMap<>();
        request.put("contentId", testContentId);
        request.put("title", title);
        request.put("information", information);
        request.put("stateCode", 1);
        request.put("cityCode", 1);
        request.put("recruitTypeCode", 1);
        request.put("maxPeople", 4);
        request.put("genderTypeCode", 1);
        request.put("isDateAgreement", false);
        request.put("hopeDate", LocalDate.now().plusDays(7).toString());
        request.put("kakaoOpenChatUrl", "https://open.kakao.com/test");
        request.put("ageGroupCodes", List.of(1, 2, 3));
        return request;
    }

    /**
     * 동행 모집 등록 요청
     */
    private ExtractableResponse<Response> 동행_모집_등록_요청(Long contentId, String title, String information) {
        Map<String, Object> request = 동행_모집_등록_요청_바디(title, information);

        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/gathering")
                .then()
                .extract();
    }

    /**
     * 동행 모집 등록 후 ID 반환
     */
    private Long 동행_모집_등록_후_ID_반환(Long contentId, String title, String information) {
        Number gatheringId = 동행_모집_등록_요청(contentId, title, information).path("data.id");
        return gatheringId != null ? gatheringId.longValue() : null;
    }

    /**
     * 동행 모집 등록 후 ID 반환 (특정 토큰 사용)
     */
    private Long 동행_모집_등록_후_ID_반환_with_token(Long contentId, String title, String information, String token) {
        Map<String, Object> request = 동행_모집_등록_요청_바디(title, information);

        Number gatheringId = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/gathering")
                .then()
                .extract()
                .path("data.id");

        return gatheringId != null ? gatheringId.longValue() : null;
    }

    /**
     * 동행 모집 조회 요청
     */
    private ExtractableResponse<Response> 동행_모집_조회_요청(Long gatheringId) {
        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/api/gathering/" + gatheringId)
                .then()
                .extract();
    }

    /**
     * 동행 모집 리스트 조회 요청
     */
    private ExtractableResponse<Response> 동행_모집_리스트_조회_요청() {
        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .queryParam("page", 1)
                .queryParam("size", 10)
                .when()
                .get("/api/gathering")
                .then()
                .extract();
    }

    /**
     * 동행 모집 수정 요청
     */
    private ExtractableResponse<Response> 동행_모집_수정_요청(Long gatheringId, String title, String information) {
        Map<String, Object> request = new HashMap<>();
        request.put("gatheringId", gatheringId);
        request.put("title", title);
        request.put("information", information);
        request.put("stateCode", 1);
        request.put("cityCode", 1);
        request.put("recruitTypeCode", 1);
        request.put("maxPeople", 5);
        request.put("genderTypeCode", 2);
        request.put("isDateAgreement", true);
        request.put("hopeDate", LocalDate.now().plusDays(10).toString());
        request.put("kakaoOpenChatUrl", "https://open.kakao.com/updated");
        request.put("ageGroupCodes", List.of(2, 3));

        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/api/gathering/" + gatheringId)
                .then()
                .extract();
    }

    /**
     * 동행 모집 삭제 요청
     */
    private ExtractableResponse<Response> 동행_모집_삭제_요청(Long gatheringId) {
        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete("/api/gathering/" + gatheringId)
                .then()
                .extract();
    }

    /**
     * 동행 참가 신청 요청
     */
    private ExtractableResponse<Response> 동행_참가_신청_요청(Long gatheringId, String requestReason) {
        Map<String, Object> request = new HashMap<>();
        request.put("gatheringId", gatheringId);
        request.put("requestReason", requestReason);

        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/gathering/apply")
                .then()
                .extract();
    }

    /**
     * 동행 찜 등록 요청
     */
    private ExtractableResponse<Response> 동행_찜_등록_요청(Long gatheringId) {
        Map<String, Object> request = new HashMap<>();
        request.put("gatheringId", gatheringId);

        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/gathering/favorite")
                .then()
                .extract();
    }

    /**
     * 동행 찜 삭제 요청
     */
    private ExtractableResponse<Response> 동행_찜_삭제_요청(Long gatheringId) {
        Map<String, Object> request = new HashMap<>();
        request.put("gatheringId", gatheringId);

        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .delete("/api/gathering/favorite")
                .then()
                .extract();
    }

    /**
     * 내 동행 목록 조회 요청
     */
    private ExtractableResponse<Response> 내_동행_목록_조회_요청() {
        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/api/gathering/my-gatherings")
                .then()
                .extract();
    }

    // ===== 검증 메서드 (응답 확인) =====

    /**
     * 동행 모집 등록 성공 확인
     */
    private void 동행_모집_등록_성공_확인(ExtractableResponse<Response> response, String expectedTitle, String expectedInfo) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.id", notNullValue())
                .body("data.title", equalTo(expectedTitle))
                .body("data.information", equalTo(expectedInfo))
                .body("data.isOwner", equalTo(true));
    }

    /**
     * 동행 모집 조회 성공 확인
     */
    private void 동행_모집_조회_성공_확인(ExtractableResponse<Response> response, Long expectedId, String expectedTitle, String expectedInfo) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.id", equalTo(expectedId.intValue()))
                .body("data.title", equalTo(expectedTitle))
                .body("data.information", equalTo(expectedInfo));
    }

    /**
     * 동행 모집 리스트 조회 성공 확인
     */
    private void 동행_모집_리스트_조회_성공_확인(ExtractableResponse<Response> response, int minSize) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.gatherings", notNullValue());

        // 최소 개수 확인
        Object data = response.path("data.gatherings");
        if (data instanceof List) {
            List<?> list = (List<?>) data;
            assert list.size() >= minSize : "Expected at least " + minSize + " gatherings, but got " + list.size();
        }
    }

    /**
     * 동행 모집 수정 성공 확인
     */
    private void 동행_모집_수정_성공_확인(ExtractableResponse<Response> response) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200));
    }

    /**
     * 동행 모집 삭제 성공 확인
     */
    private void 동행_모집_삭제_성공_확인(ExtractableResponse<Response> response) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200));
    }

    /**
     * 동행 참가 신청 성공 확인
     */
    private void 동행_참가_신청_성공_확인(ExtractableResponse<Response> response) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200));
    }

    /**
     * 동행 찜 등록 성공 확인
     */
    private void 동행_찜_등록_성공_확인(ExtractableResponse<Response> response, Long expectedGatheringId) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.isFavorite", equalTo(true))
                .body("data.count", notNullValue());
    }

    /**
     * 동행 찜 삭제 성공 확인
     */
    private void 동행_찜_삭제_성공_확인(ExtractableResponse<Response> response) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200));
    }

    /**
     * 내 동행 목록 조회 성공 확인
     */
    private void 내_동행_목록_조회_성공_확인(ExtractableResponse<Response> response) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data", notNullValue())
                .body("data.ingList", notNullValue())
                .body("data.createdList", notNullValue())
                .body("data.endedList", notNullValue());
    }
}
