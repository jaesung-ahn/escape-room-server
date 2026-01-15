package com.wiiee.server.api.acceptance.wbti;

import com.wiiee.server.api.AcceptanceTest;
import com.wiiee.server.api.domain.wbti.WbtiRepository;
import com.wiiee.server.common.domain.wbti.Wbti;
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
import java.util.Map;

import static org.hamcrest.Matchers.*;

@DisplayName("Wbti API 인수 테스트")
class WbtiAcceptanceTest extends AcceptanceTest {

    @Autowired
    private WbtiRepository wbtiRepository;

    private String accessToken;
    private Long testWbtiId;

    @BeforeEach
    void setUpData() {
        // Wbti 테스트 데이터 생성
        Wbti testWbti = new Wbti("테스트 잼핏", null, "테스트,잼핏", "테스트용 잼핏 설명");
        testWbti = wbtiRepository.save(testWbti);
        testWbtiId = testWbti.getId();

        // User 생성 및 토큰 발급
        Map<String, Object> userResponse = 회원가입_후_응답_반환("wbti_user@example.com", "WBTI테스터", "pass123!");
        accessToken = (String) userResponse.get("accessToken");
    }

    // ===== 테스트 케이스 =====

    @Test
    @DisplayName("WBTI 목록 조회")
    void getWbtiList() {
        // when: WBTI 목록 조회
        ExtractableResponse<Response> response = WBTI_목록_조회_요청();

        // then: 성공 응답 및 WBTI 리스트 존재
        WBTI_목록_조회_성공_확인(response);
    }

    @Test
    @DisplayName("WBTI 저장")
    void saveWbti() {
        // given: WBTI 목록 조회하여 첫 번째 WBTI ID 가져오기
        Long wbtiId = WBTI_목록에서_첫번째_ID_가져오기();

        // when: WBTI 저장
        ExtractableResponse<Response> response = WBTI_저장_요청(wbtiId);

        // then: 성공 응답
        WBTI_저장_성공_확인(response);
    }

    @Test
    @DisplayName("WBTI 저장 - 존재하지 않는 WBTI ID")
    void saveWbti_notFound() {
        // given: 존재하지 않는 WBTI ID
        Long invalidWbtiId = 99999L;

        // when: 잘못된 WBTI ID로 저장 시도
        ExtractableResponse<Response> response = WBTI_저장_요청(invalidWbtiId);

        // then: ResourceNotFoundException으로 에러 코드 반환
        response.response()
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("code", equalTo(8120));  // ERROR_WBTI_NOT_FOUND
    }

    @Test
    @DisplayName("WBTI 목록 조회 - 인증 없이 요청 시 실패")
    void getWbtiList_unauthorized() {
        // when: 인증 없이 WBTI 목록 조회
        ExtractableResponse<Response> response = RestAssured.given()
                .when()
                .get("/api/wbti")
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
     * WBTI 목록 조회 요청
     */
    private ExtractableResponse<Response> WBTI_목록_조회_요청() {
        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/api/wbti")
                .then()
                .extract();
    }

    /**
     * WBTI 목록에서 첫 번째 ID 가져오기
     */
    private Long WBTI_목록에서_첫번째_ID_가져오기() {
        ExtractableResponse<Response> response = WBTI_목록_조회_요청();
        Number wbtiId = response.path("data.zamfitTest[0].id");
        return wbtiId != null ? wbtiId.longValue() : testWbtiId;  // 없으면 테스트 데이터 ID
    }

    /**
     * WBTI 저장 요청
     */
    private ExtractableResponse<Response> WBTI_저장_요청(Long wbtiId) {
        Map<String, Object> request = new HashMap<>();
        request.put("wbtiId", wbtiId);

        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/wbti")
                .then()
                .extract();
    }

    // ===== 헬퍼 메서드 (검증) =====

    /**
     * WBTI 목록 조회 성공 확인
     */
    private void WBTI_목록_조회_성공_확인(ExtractableResponse<Response> response) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.zamfitTest", notNullValue());
    }

    /**
     * WBTI 저장 성공 확인
     */
    private void WBTI_저장_성공_확인(ExtractableResponse<Response> response) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200));
    }
}
