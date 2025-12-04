package com.wiiee.server.api.acceptance.user;

import com.wiiee.server.api.AcceptanceTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;

@DisplayName("User API 인수 테스트")
class UserAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("일반 회원가입")
    void signUp() {
        // given: 회원가입 정보
        String email = "test@example.com";
        String nickname = "테스터";
        String password = "password123!";

        // when: 회원가입 요청
        ExtractableResponse<Response> response = 회원가입_요청(email, nickname, password);

        // then: 성공 응답과 토큰 반환
        회원가입_성공_확인(response, email, nickname);
    }

    @Test
    @DisplayName("일반 로그인")
    void login() {
        // given: 기존 회원 생성
        String email = "login_test@example.com";
        String password = "password123!";
        String nickname = "로그인테스터";
        회원가입_요청(email, nickname, password);

        // when: 로그인 요청
        ExtractableResponse<Response> response = 로그인_요청(email, password);

        // then: 성공 응답과 토큰 반환
        로그인_성공_확인(response, email, nickname);
    }

    @Test
    @DisplayName("닉네임 중복 체크 - 사용 가능")
    void checkNickname_available() {
        // given: 사용되지 않은 닉네임
        String nickname = "사용가능닉네임";

        // when: 닉네임 중복 체크
        ExtractableResponse<Response> response = 닉네임_중복_체크_요청(nickname);

        // then: false (존재하지 않음 = false)
        닉네임_사용가능_확인(response);
    }

    @Test
    @DisplayName("닉네임 중복 체크 - 이미 사용 중")
    void checkNickname_duplicate() {
        // given: 기존 회원 생성
        String nickname = "중복닉네임";
        회원가입_요청("duplicate@example.com", nickname, "password123!");

        // when: 동일한 닉네임으로 중복 체크
        ExtractableResponse<Response> response = 닉네임_중복_체크_요청(nickname);

        // then: true (중복됨 = true, 사용 불가)
        닉네임_중복_확인(response);
    }

    @Test
    @DisplayName("마이페이지 조회 - 인증 필요")
    void getMyPage() {
        // given: 회원가입 후 토큰 획득
        String nickname = "마이페이지유저";
        String accessToken = 회원가입_후_토큰_발급("mypage@example.com", nickname, "password123!");

        // when: 마이페이지 조회
        ExtractableResponse<Response> response = 마이페이지_조회_요청(accessToken);

        // then: 성공 응답
        마이페이지_조회_성공_확인(response, nickname);
    }

    @Test
    @DisplayName("유저 조회")
    void getUser() {
        // given: 기존 회원 생성
        String nickname = "조회유저";
        ExtractableResponse<Response> signupResponse = 회원가입_요청("getuser@example.com", nickname, "password123!");

        // 회원가입 성공 확인
        signupResponse.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200));

        // ID 추출
        Integer userId = signupResponse.path("data.id");
        if (userId == null) {
            throw new AssertionError("회원가입 응답에 data.id가 없습니다. 응답: " + signupResponse.asString());
        }

        // when: 유저 조회
        ExtractableResponse<Response> response = 유저_조회_요청(userId);

        // then: 성공 응답
        유저_조회_성공_확인(response, nickname);
    }

    @Test
    @DisplayName("로그아웃")
    void logout() {
        // given: 회원가입 후 토큰 획득
        String accessToken = 회원가입_후_토큰_발급("logout@example.com", "로그아웃유저", "password123!");

        // when: 로그아웃 요청
        ExtractableResponse<Response> response = 로그아웃_요청(accessToken);

        // then: 성공 응답
        로그아웃_성공_확인(response);
    }

    // ===== 헬퍼 메서드 =====

    /**
     * 회원가입 요청
     */
    private ExtractableResponse<Response> 회원가입_요청(String email, String nickname, String password) {
        Map<String, Object> request = new HashMap<>();
        request.put("email", email);
        request.put("nickname", nickname);
        request.put("password", password);

        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/user")
                .then()
                .extract();
    }

    /**
     * 회원가입 후 토큰 발급
     */
    private String 회원가입_후_토큰_발급(String email, String nickname, String password) {
        return 회원가입_요청(email, nickname, password)
                .path("data.accessToken");
    }

    /**
     * 회원가입 후 ID 반환
     */
    private Integer 회원가입_후_ID_반환(String email, String nickname, String password) {
        return 회원가입_요청(email, nickname, password)
                .path("data.id");
    }

    /**
     * 로그인 요청
     */
    private ExtractableResponse<Response> 로그인_요청(String email, String password) {
        Map<String, Object> request = new HashMap<>();
        request.put("email", email);
        request.put("password", password);

        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/user/login")
                .then()
                .extract();
    }

    /**
     * 닉네임 중복 체크 요청
     */
    private ExtractableResponse<Response> 닉네임_중복_체크_요청(String nickname) {
        return RestAssured.given()
                .queryParam("nickname", nickname)
                .when()
                .get("/api/user/check-nickname")
                .then()
                .extract();
    }

    /**
     * 마이페이지 조회 요청
     */
    private ExtractableResponse<Response> 마이페이지_조회_요청(String accessToken) {
        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/api/user/my-page")
                .then()
                .extract();
    }

    /**
     * 유저 조회 요청
     */
    private ExtractableResponse<Response> 유저_조회_요청(Integer userId) {
        return RestAssured.given()
                .when()
                .get("/api/user/" + userId)
                .then()
                .extract();
    }

    /**
     * 로그아웃 요청
     */
    private ExtractableResponse<Response> 로그아웃_요청(String accessToken) {
        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .when()
                .post("/api/user/logout")
                .then()
                .extract();
    }

    // ===== 검증 메서드 =====

    /**
     * 회원가입 성공 확인
     */
    private void 회원가입_성공_확인(ExtractableResponse<Response> response, String email, String nickname) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.email", equalTo(email))
                .body("data.nickname", equalTo(nickname))
                .body("data.accessToken", notNullValue())
                .body("data.refreshToken", notNullValue());
    }

    /**
     * 로그인 성공 확인
     */
    private void 로그인_성공_확인(ExtractableResponse<Response> response, String email, String nickname) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.email", equalTo(email))
                .body("data.nickname", equalTo(nickname))
                .body("data.accessToken", notNullValue())
                .body("data.refreshToken", notNullValue());
    }

    /**
     * 닉네임 사용 가능 확인
     */
    private void 닉네임_사용가능_확인(ExtractableResponse<Response> response) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data", equalTo(false));
    }

    /**
     * 닉네임 중복 확인
     */
    private void 닉네임_중복_확인(ExtractableResponse<Response> response) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data", equalTo(true));
    }

    /**
     * 마이페이지 조회 성공 확인
     */
    private void 마이페이지_조회_성공_확인(ExtractableResponse<Response> response, String nickname) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data", notNullValue())
                .body("data.nickname", equalTo(nickname));
    }

    /**
     * 유저 조회 성공 확인
     */
    private void 유저_조회_성공_확인(ExtractableResponse<Response> response, String nickname) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.id", notNullValue())
                .body("data.nickname", equalTo(nickname));
    }

    /**
     * 로그아웃 성공 확인
     */
    private void 로그아웃_성공_확인(ExtractableResponse<Response> response) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200));
    }
}
