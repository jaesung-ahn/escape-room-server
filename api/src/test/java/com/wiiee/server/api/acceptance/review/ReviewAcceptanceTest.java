package com.wiiee.server.api.acceptance.review;

import com.wiiee.server.api.AcceptanceTest;
import com.wiiee.server.api.domain.admin.AdminRepository;
import com.wiiee.server.api.domain.content.review.ReviewRepository;
import com.wiiee.server.common.domain.admin.AdminUser;
import com.wiiee.server.common.domain.content.review.Review;
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

@DisplayName("Review API 인수 테스트")
class ReviewAcceptanceTest extends AcceptanceTest {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    private Long testCompanyId;
    private Long testContentId;
    private String accessToken;
    private Long testUserId;

    @BeforeEach
    void setUpData() {
        // 1. AdminUser 생성
        AdminUser admin = AdminUser.of("review_test_admin@wiiee.com", "admin123!");
        Long adminId = adminRepository.save(admin).getId();

        // 2. Company 생성
        testCompanyId = 업체_생성_후_ID_반환(adminId, "리뷰 테스트 방탈출");

        // 3. User 생성 및 토큰 발급
        Map<String, Object> userResponse = 회원가입_후_응답_반환("review_user@example.com", "리뷰테스터", "pass123!");
        accessToken = (String) userResponse.get("accessToken");
        testUserId = ((Number) userResponse.get("userId")).longValue();

        // 4. Content 생성
        testContentId = 컨텐츠_생성_후_ID_반환("리뷰 테스트용 방탈출");
    }

    // ===== 테스트 케이스 =====

    @Test
    @DisplayName("리뷰 작성")
    void createReview() {
        // given: 리뷰 정보
        String message = "정말 재미있었어요!";
        Double rating = 4.5;

        // when: 리뷰 작성 요청
        ExtractableResponse<Response> response = 리뷰_작성_요청(testContentId, message, rating);

        // then: 성공 응답
        리뷰_작성_성공_확인(response, message, rating);
    }

    @Test
    @DisplayName("단일 리뷰 조회")
    void getReview() {
        // given: 기존 리뷰 생성
        String message = "너무 어려웠어요";
        Double rating = 3.5;
        Long reviewId = 리뷰_작성_후_ID_반환(testContentId, message, rating);

        // when: 리뷰 조회
        ExtractableResponse<Response> response = 리뷰_조회_요청(reviewId);

        // then: 성공 응답
        리뷰_조회_성공_확인(response, reviewId, message, rating);
    }

    @Test
    @DisplayName("컨텐츠별 리뷰 리스트 조회")
    void getReviewsByContentId() {
        // given: 동일 컨텐츠에 여러 리뷰 생성 및 승인
        리뷰_작성_및_승인_후_ID_반환(testContentId, "재미있어요", 5.0);
        리뷰_작성_및_승인_후_ID_반환(testContentId, "보통이에요", 3.0);
        리뷰_작성_및_승인_후_ID_반환(testContentId, "어려웠어요", 4.0);

        // when: 컨텐츠별 리뷰 리스트 조회
        ExtractableResponse<Response> response = 컨텐츠별_리뷰_리스트_조회_요청(testContentId);

        // then: 성공 응답 (최소 3개 이상)
        리뷰_리스트_조회_성공_확인(response, 3);
    }

    @Test
    @DisplayName("전체 리뷰 리스트 조회")
    void getReviews() {
        // given: 여러 리뷰 생성 및 승인
        리뷰_작성_및_승인_후_ID_반환(testContentId, "첫 번째 리뷰", 5.0);
        리뷰_작성_및_승인_후_ID_반환(testContentId, "두 번째 리뷰", 4.0);

        // when: 전체 리뷰 리스트 조회
        ExtractableResponse<Response> response = 전체_리뷰_리스트_조회_요청();

        // then: 성공 응답 (최소 2개 이상)
        리뷰_리스트_조회_성공_확인(response, 2);
    }

    @Test
    @DisplayName("리뷰 수정")
    void updateReview() {
        // given: 기존 리뷰 생성
        String originalMessage = "처음 작성한 리뷰";
        Double originalRating = 3.0;
        Long reviewId = 리뷰_작성_후_ID_반환(testContentId, originalMessage, originalRating);

        // when: 리뷰 수정 요청
        String updatedMessage = "수정된 리뷰 내용";
        Double updatedRating = 4.5;
        ExtractableResponse<Response> response = 리뷰_수정_요청(reviewId, updatedMessage, updatedRating);

        // then: 성공 응답 및 내용 변경 확인
        리뷰_수정_성공_확인(response, updatedMessage, updatedRating);
    }

    @Test
    @DisplayName("리뷰 삭제")
    void deleteReview() {
        // given: 기존 리뷰 생성
        Long reviewId = 리뷰_작성_후_ID_반환(testContentId, "삭제할 리뷰", 4.0);

        // when: 리뷰 삭제 요청
        ExtractableResponse<Response> response = 리뷰_삭제_요청(reviewId);

        // then: 성공 응답
        리뷰_삭제_성공_확인(response);
    }

    @Test
    @DisplayName("내 리뷰 리스트 조회")
    void getMyReviews() {
        // given: 여러 리뷰 생성 및 승인
        리뷰_작성_및_승인_후_ID_반환(testContentId, "내 첫 번째 리뷰", 5.0);
        리뷰_작성_및_승인_후_ID_반환(testContentId, "내 두 번째 리뷰", 4.5);

        // when: 내 리뷰 리스트 조회
        ExtractableResponse<Response> response = 내_리뷰_리스트_조회_요청();

        // then: 성공 응답 (최소 2개 이상)
        리뷰_리스트_조회_성공_확인(response, 2);
    }

    @Test
    @DisplayName("리뷰 작성 - 인증 없이 요청 시 실패")
    void createReview_unauthorized() {
        // given: 리뷰 정보
        Map<String, Object> request = 리뷰_작성_요청_바디("인증 테스트 리뷰", 4.0);

        // when: 인증 없이 리뷰 작성
        ExtractableResponse<Response> response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/review/content/" + testContentId)
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
     * 리뷰 작성 요청 바디 생성
     */
    private Map<String, Object> 리뷰_작성_요청_바디(String message, Double rating) {
        Map<String, Object> request = new HashMap<>();
        request.put("message", message);
        request.put("rating", rating);
        request.put("joinNumber", 4);
        request.put("imageIds", List.of());
        request.put("realGatherDate", LocalDate.now().toString());
        return request;
    }

    /**
     * 리뷰 작성 요청
     */
    private ExtractableResponse<Response> 리뷰_작성_요청(Long contentId, String message, Double rating) {
        Map<String, Object> request = 리뷰_작성_요청_바디(message, rating);

        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/review/content/" + contentId)
                .then()
                .extract();
    }

    /**
     * 리뷰 작성 후 ID 반환
     */
    private Long 리뷰_작성_후_ID_반환(Long contentId, String message, Double rating) {
        Number reviewId = 리뷰_작성_요청(contentId, message, rating).path("data.id");
        return reviewId != null ? reviewId.longValue() : null;
    }

    /**
     * 리뷰 승인 처리 (테스트용)
     */
    private void 리뷰_승인_처리(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow();
        review.updateApproval(true);
        reviewRepository.save(review);
    }

    /**
     * 리뷰 작성 및 승인 처리 후 ID 반환
     */
    private Long 리뷰_작성_및_승인_후_ID_반환(Long contentId, String message, Double rating) {
        Long reviewId = 리뷰_작성_후_ID_반환(contentId, message, rating);
        리뷰_승인_처리(reviewId);
        return reviewId;
    }

    /**
     * 리뷰 조회 요청
     */
    private ExtractableResponse<Response> 리뷰_조회_요청(Long reviewId) {
        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/api/review/" + reviewId)
                .then()
                .extract();
    }

    /**
     * 컨텐츠별 리뷰 리스트 조회 요청
     */
    private ExtractableResponse<Response> 컨텐츠별_리뷰_리스트_조회_요청(Long contentId) {
        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .queryParam("page", 1)
                .queryParam("size", 10)
                .when()
                .get("/api/review/content/" + contentId)
                .then()
                .extract();
    }

    /**
     * 전체 리뷰 리스트 조회 요청
     */
    private ExtractableResponse<Response> 전체_리뷰_리스트_조회_요청() {
        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .queryParam("page", 1)
                .queryParam("size", 10)
                .when()
                .get("/api/review")
                .then()
                .extract();
    }

    /**
     * 리뷰 수정 요청
     */
    private ExtractableResponse<Response> 리뷰_수정_요청(Long reviewId, String message, Double rating) {
        Map<String, Object> request = new HashMap<>();
        request.put("message", message);
        request.put("rating", rating);
        request.put("imageIds", List.of());

        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/api/review/" + reviewId)
                .then()
                .extract();
    }

    /**
     * 리뷰 삭제 요청
     */
    private ExtractableResponse<Response> 리뷰_삭제_요청(Long reviewId) {
        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .delete("/api/review/" + reviewId)
                .then()
                .extract();
    }

    /**
     * 내 리뷰 리스트 조회 요청
     */
    private ExtractableResponse<Response> 내_리뷰_리스트_조회_요청() {
        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .queryParam("page", 1)
                .queryParam("size", 10)
                .when()
                .get("/api/review/my-page")
                .then()
                .extract();
    }

    // ===== 검증 메서드 (응답 확인) =====

    /**
     * 리뷰 작성 성공 확인
     */
    private void 리뷰_작성_성공_확인(ExtractableResponse<Response> response, String expectedMessage, Double expectedRating) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.id", notNullValue())
                .body("data.message", equalTo(expectedMessage))
                .body("data.rating", equalTo(expectedRating.floatValue()))
                .body("data.nickname", notNullValue())
                .body("data.isOwner", equalTo(true));
    }

    /**
     * 리뷰 조회 성공 확인
     */
    private void 리뷰_조회_성공_확인(ExtractableResponse<Response> response, Long expectedId, String expectedMessage, Double expectedRating) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.id", equalTo(expectedId.intValue()))
                .body("data.message", equalTo(expectedMessage))
                .body("data.rating", equalTo(expectedRating.floatValue()))
                .body("data.isOwner", equalTo(true));
    }

    /**
     * 리뷰 리스트 조회 성공 확인
     */
    private void 리뷰_리스트_조회_성공_확인(ExtractableResponse<Response> response, int minSize) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.reviews", notNullValue());

        // 최소 개수 확인
        Object data = response.path("data.reviews");
        if (data instanceof List) {
            List<?> list = (List<?>) data;
            assert list.size() >= minSize : "Expected at least " + minSize + " reviews, but got " + list.size();
        }
    }

    /**
     * 리뷰 수정 성공 확인
     */
    private void 리뷰_수정_성공_확인(ExtractableResponse<Response> response, String expectedMessage, Double expectedRating) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.message", equalTo(expectedMessage))
                .body("data.rating", equalTo(expectedRating.floatValue()));
    }

    /**
     * 리뷰 삭제 성공 확인
     */
    private void 리뷰_삭제_성공_확인(ExtractableResponse<Response> response) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200));
    }
}
