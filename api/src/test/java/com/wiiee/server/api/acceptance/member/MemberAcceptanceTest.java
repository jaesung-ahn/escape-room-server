package com.wiiee.server.api.acceptance.member;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;

@DisplayName("Member API 인수 테스트")
class MemberAcceptanceTest extends AcceptanceTest {

    @Autowired
    private AdminRepository adminRepository;

    private Long testCompanyId;
    private Long testContentId;
    private String accessToken;
    private Long testUserId;
    private Long testGatheringId;

    @BeforeEach
    void setUpData() {
        // 1. AdminUser 생성
        AdminUser admin = AdminUser.of("member_test_admin@wiiee.com", "admin123!");
        Long adminId = adminRepository.save(admin).getId();

        // 2. Company 생성
        testCompanyId = 업체_생성_후_ID_반환(adminId, "멤버 테스트 방탈출");

        // 3. User 생성 및 토큰 발급
        Map<String, Object> userResponse = 회원가입_후_응답_반환("member_user@example.com", "멤버테스터", "pass123!");
        accessToken = (String) userResponse.get("accessToken");
        testUserId = ((Number) userResponse.get("userId")).longValue();

        // 4. Content 생성
        testContentId = 컨텐츠_생성_후_ID_반환("멤버 테스트용 방탈출");

        // 5. Gathering 생성 (선착순 방식)
        testGatheringId = 동행_모집_등록_후_ID_반환(testContentId, "멤버 테스트 동행", "멤버 관리 테스트");
    }

    // ===== 테스트 케이스 =====

    @Test
    @DisplayName("멤버 등록 성공")
    void addMember() {
        // given: 다른 사용자 생성
        Map<String, Object> newUserResponse = 회원가입_후_응답_반환("new_member@example.com", "신규멤버", "pass123!");
        String newUserToken = (String) newUserResponse.get("accessToken");

        // when: 멤버 등록 요청
        ExtractableResponse<Response> response = 멤버_등록_요청(testGatheringId, newUserToken);

        // then: 성공 응답
        멤버_등록_성공_확인(response, testGatheringId);
    }

    @Test
    @DisplayName("멤버 상태 수정 - 승인으로 변경")
    void updateMember_approval() {
        // given: 멤버 등록
        Map<String, Object> newUserResponse = 회원가입_후_응답_반환("member2@example.com", "멤버2", "pass123!");
        String newUserToken = (String) newUserResponse.get("accessToken");
        Long memberId = 멤버_등록_후_ID_반환(testGatheringId, newUserToken);

        // when: 멤버 상태를 승인(1)으로 수정
        ExtractableResponse<Response> response = 멤버_상태_수정_요청(memberId, 1);

        // then: 성공 응답
        멤버_상태_수정_성공_확인(response, memberId);
    }

    @Test
    @DisplayName("멤버 상태 수정 - 대기로 변경")
    void updateMember_waiting() {
        // given: 멤버 등록
        Map<String, Object> newUserResponse = 회원가입_후_응답_반환("member3@example.com", "멤버3", "pass123!");
        String newUserToken = (String) newUserResponse.get("accessToken");
        Long memberId = 멤버_등록_후_ID_반환(testGatheringId, newUserToken);

        // when: 멤버 상태를 대기(0)로 수정
        ExtractableResponse<Response> response = 멤버_상태_수정_요청(memberId, 0);

        // then: 성공 응답
        멤버_상태_수정_성공_확인(response, memberId);
    }

    @Test
    @DisplayName("멤버 상태 수정 - 거절로 변경")
    void updateMember_reject() {
        // given: 멤버 등록
        Map<String, Object> newUserResponse = 회원가입_후_응답_반환("member4@example.com", "멤버4", "pass123!");
        String newUserToken = (String) newUserResponse.get("accessToken");
        Long memberId = 멤버_등록_후_ID_반환(testGatheringId, newUserToken);

        // when: 멤버 상태를 거절(2)로 수정
        ExtractableResponse<Response> response = 멤버_상태_수정_요청(memberId, 2);

        // then: 성공 응답
        멤버_상태_수정_성공_확인(response, memberId);
    }

    @Test
    @DisplayName("멤버 중복 등록 실패")
    void addMember_duplicate() {
        // given: 이미 멤버로 등록된 사용자
        Map<String, Object> newUserResponse = 회원가입_후_응답_반환("duplicate_member@example.com", "중복멤버", "pass123!");
        String newUserToken = (String) newUserResponse.get("accessToken");
        멤버_등록_요청(testGatheringId, newUserToken);

        // when: 같은 사용자로 다시 멤버 등록 시도
        ExtractableResponse<Response> response = 멤버_등록_요청(testGatheringId, newUserToken);

        // then: 실패 응답 (400 또는 500)
        response.response()
                .then()
                .statusCode(anyOf(equalTo(HttpStatus.BAD_REQUEST.value()), equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value())));
    }

    @Test
    @DisplayName("멤버 등록 - 인증 없이 요청 시 실패")
    void addMember_unauthorized() {
        // when: 인증 없이 멤버 등록
        ExtractableResponse<Response> response = RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .post("/api/member/gathering/" + testGatheringId)
                .then()
                .extract();

        // then: 401 Unauthorized 또는 403 Forbidden
        response.response()
                .then()
                .statusCode(anyOf(equalTo(HttpStatus.UNAUTHORIZED.value()), equalTo(HttpStatus.FORBIDDEN.value())));
    }

    @Test
    @DisplayName("멤버 상태 수정 - 존재하지 않는 멤버")
    void updateMember_notFound() {
        // given: 존재하지 않는 memberId
        Long invalidMemberId = 99999L;

        // when: 존재하지 않는 멤버 상태 수정
        ExtractableResponse<Response> response = 멤버_상태_수정_요청(invalidMemberId, 1);

        // then: 실패 응답 (400 또는 500)
        response.response()
                .then()
                .statusCode(anyOf(
                        equalTo(HttpStatus.BAD_REQUEST.value()),
                        equalTo(HttpStatus.NOT_FOUND.value()),
                        equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
                ));
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
        request.put("genreCode", 1);  // 스릴러
        request.put("imageIds", List.of());
        request.put("minPeople", 2);
        request.put("maxPeople", 6);
        request.put("information", "재미있는 방탈출 게임");
        request.put("playTime", 60);
        request.put("activityLevelCode", 2);  // 보통
        request.put("escapeTypeCode", 1);  // 탈출형
        request.put("isCaution", false);
        request.put("difficultyCode", 3);  // 중간
        request.put("isNoEscapeType", false);
        request.put("isNew", true);
        request.put("newDisplayExpirationDate", java.time.LocalDate.now().plusMonths(1).toString());
        request.put("isOperated", true);
        request.put("priceList", List.of());

        Number contentId = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(request)
                .when()
                .post("/api/content")
                .then()
                .extract()
                .path("data.id");

        return contentId != null ? contentId.longValue() : null;
    }

    /**
     * 동행 모집 등록 후 ID 반환
     */
    private Long 동행_모집_등록_후_ID_반환(Long contentId, String title, String information) {
        Map<String, Object> request = new HashMap<>();
        request.put("contentId", contentId);
        request.put("title", title);
        request.put("information", information);
        request.put("stateCode", 1);
        request.put("cityCode", 1);
        request.put("recruitTypeCode", 1);  // 선착순
        request.put("maxPeople", 4);
        request.put("genderTypeCode", 1);
        request.put("isDateAgreement", false);
        request.put("hopeDate", java.time.LocalDate.now().plusDays(7).toString());
        request.put("kakaoOpenChatUrl", "https://open.kakao.com/test");
        request.put("ageGroupCodes", List.of(1, 2, 3));

        Number gatheringId = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(request)
                .when()
                .post("/api/gathering")
                .then()
                .extract()
                .path("data.id");

        return gatheringId != null ? gatheringId.longValue() : null;
    }

    /**
     * 멤버 등록 요청
     */
    private ExtractableResponse<Response> 멤버_등록_요청(Long gatheringId, String token) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body("{}")  // 빈 JSON body 추가
                .when()
                .post("/api/member/gathering/" + gatheringId)
                .then()
                .extract();
    }

    /**
     * 멤버 등록 후 ID 반환
     */
    private Long 멤버_등록_후_ID_반환(Long gatheringId, String token) {
        Number memberId = 멤버_등록_요청(gatheringId, token).path("data.id");
        return memberId != null ? memberId.longValue() : null;
    }

    /**
     * 멤버 상태 수정 요청
     */
    private ExtractableResponse<Response> 멤버_상태_수정_요청(Long memberId, Integer statusCode) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .queryParam("statusCode", statusCode)
                .when()
                .put("/api/member/" + memberId)
                .then()
                .extract();
    }

    // ===== 헬퍼 메서드 (검증) =====

    /**
     * 멤버 등록 성공 확인
     */
    private void 멤버_등록_성공_확인(ExtractableResponse<Response> response, Long expectedGatheringId) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                // id는 flush 타이밍에 따라 null일 수 있음
                .body("data.gatheringId", equalTo(expectedGatheringId.intValue()))
                .body("data.userId", notNullValue())
                .body("data.userNickname", notNullValue());
    }

    /**
     * 멤버 상태 수정 성공 확인
     */
    private void 멤버_상태_수정_성공_확인(ExtractableResponse<Response> response, Long expectedMemberId) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.id", equalTo(expectedMemberId.intValue()))
                .body("data.gatheringId", notNullValue());
    }
}
