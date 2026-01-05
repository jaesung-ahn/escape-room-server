package com.wiiee.server.api.acceptance.content;

import com.wiiee.server.api.AcceptanceTest;
import com.wiiee.server.api.acceptance.company.CompanyAcceptanceTest;
import com.wiiee.server.api.domain.admin.AdminRepository;
import com.wiiee.server.api.domain.user.UserRepository;
import com.wiiee.server.api.infrastructure.jwt.JwtTokenProvider;
import com.wiiee.server.common.domain.admin.AdminUser;
import com.wiiee.server.common.domain.user.Password;
import com.wiiee.server.common.domain.user.User;
import com.wiiee.server.common.domain.user.UserRole;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;

@DisplayName("Content API 인수 테스트")
public class ContentAcceptanceTest extends AcceptanceTest {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long testCompanyId;
    private String accessToken;  // 일반 USER 토큰
    private String adminToken;   // ADMIN 토큰

    @BeforeEach
    void setUpData() {
        // 1. AdminUser 생성 (Company에 필요)
        AdminUser admin = AdminUser.of("content_test_admin@wiiee.com", "admin123!");
        Long adminId = adminRepository.save(admin).getId();

        // 2. ADMIN 역할 사용자 생성 및 토큰 발급
        Password adminPassword = Password.of("password123!", passwordEncoder);
        User adminUser = User.ofWithRole("admin@example.com", "adminUser", adminPassword, UserRole.ADMIN);
        User savedAdminUser = userRepository.save(adminUser);
        adminToken = jwtTokenProvider.createToken(savedAdminUser.getEmail()).getAccessToken();

        // 3. Company 생성 (Content가 속할 업체) - ADMIN 권한 필요
        testCompanyId = 업체_생성_후_ID_반환(adminId, "테스트 방탈출");

        // 4. 일반 User 생성 및 토큰 발급 (인증 필요)
        Map<String, Object> userResponse = 회원가입_후_응답_반환("content_user@example.com", "컨텐츠테스터", "pass123!");
        accessToken = (String) userResponse.get("accessToken");
    }

    // ===== 테스트 케이스 =====

    @Test
    @DisplayName("컨텐츠 생성")
    void createContent() {
        // given: 컨텐츠 정보
        String contentName = "살인마의 추격";

        // when: 컨텐츠 생성 요청
        ExtractableResponse<Response> response = 컨텐츠_생성_요청(contentName);

        // then: 성공 응답
        컨텐츠_생성_성공_확인(response, contentName);
    }

    @Test
    @DisplayName("컨텐츠 상세 조회")
    void getContent() {
        // given: 기존 컨텐츠 생성
        String contentName = "미스터리 저택";
        Long contentId = 컨텐츠_생성_후_ID_반환(contentName);

        // when: 컨텐츠 상세 조회
        ExtractableResponse<Response> response = 컨텐츠_조회_요청(contentId);

        // then: 성공 응답
        컨텐츠_조회_성공_확인(response, contentId, contentName);
    }

    @Test
    @DisplayName("컨텐츠 리스트 조회")
    void getContents() {
        // given: 여러 컨텐츠 생성
        컨텐츠_생성_요청("스릴러 방탈출 1");
        컨텐츠_생성_요청("스릴러 방탈출 2");
        컨텐츠_생성_요청("스릴러 방탈출 3");

        // when: 컨텐츠 리스트 조회
        ExtractableResponse<Response> response = 컨텐츠_리스트_조회_요청();

        // then: 성공 응답 (최소 3개 이상)
        컨텐츠_리스트_조회_성공_확인(response, 3);
    }

    @Test
    @DisplayName("컨텐츠 찜 등록")
    void addContentFavorite() {
        // given: 기존 컨텐츠 생성
        Long contentId = 컨텐츠_생성_후_ID_반환("찜할 컨텐츠");

        // when: 컨텐츠 찜 등록
        ExtractableResponse<Response> response = 컨텐츠_찜_등록_요청(contentId);

        // then: 성공 응답
        컨텐츠_찜_등록_성공_확인(response, contentId);
    }

    @Test
    @DisplayName("컨텐츠 찜 삭제")
    void deleteContentFavorite() {
        // given: 컨텐츠 생성 및 찜 등록
        Long contentId = 컨텐츠_생성_후_ID_반환("찜 삭제 테스트");
        컨텐츠_찜_등록_요청(contentId);

        // when: 컨텐츠 찜 삭제
        ExtractableResponse<Response> response = 컨텐츠_찜_삭제_요청(contentId);

        // then: 성공 응답
        컨텐츠_찜_삭제_성공_확인(response);
    }

    @Test
    @DisplayName("내 찜리스트 조회")
    void getMyFavorites() {
        // given: 여러 컨텐츠 생성 및 찜 등록
        Long contentId1 = 컨텐츠_생성_후_ID_반환("찜 컨텐츠 1");
        Long contentId2 = 컨텐츠_생성_후_ID_반환("찜 컨텐츠 2");
        컨텐츠_찜_등록_요청(contentId1);
        컨텐츠_찜_등록_요청(contentId2);

        // when: 내 찜리스트 조회
        ExtractableResponse<Response> response = 내_찜리스트_조회_요청();

        // then: 성공 응답 (최소 2개 이상)
        내_찜리스트_조회_성공_확인(response, 2);
    }

    @Test
    @DisplayName("컨텐츠 조회 - 인증 없이 요청 시 실패")
    void getContent_unauthorized() {
        // given: 기존 컨텐츠 생성
        Long contentId = 컨텐츠_생성_후_ID_반환("인증 테스트");

        // when: 인증 없이 컨텐츠 조회
        ExtractableResponse<Response> response = RestAssured.given()
                .when()
                .get("/api/content/" + contentId)
                .then()
                .extract();

        // then: 401 Unauthorized 또는 403 Forbidden
        response.response()
                .then()
                .statusCode(anyOf(equalTo(HttpStatus.UNAUTHORIZED.value()), equalTo(HttpStatus.FORBIDDEN.value())));
    }

    // ===== Public Static 헬퍼 메서드 (다른 테스트에서 사용) =====

    /**
     * 컨텐츠 생성 (기본 메소드)
     * 모든 컨텐츠 생성 관련 메소드의 기반이 되는 메소드
     *
     * @param adminToken ADMIN 권한 토큰
     * @param companyId 업체 ID
     * @param name 컨텐츠명
     * @return ExtractableResponse - 필요한 정보를 자유롭게 추출 가능
     */
    public static ExtractableResponse<Response> 컨텐츠_생성(String adminToken, Long companyId, String name) {
        Map<String, Object> request = new HashMap<>();
        request.put("companyId", companyId);
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

        return RestAssured.given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/content")
                .then()
                .extract();
    }

    /**
     * 컨텐츠 생성 후 ID 반환
     */
    public static Long 컨텐츠_생성_후_ID_반환(String adminToken, Long companyId, String name) {
        Number contentId = 컨텐츠_생성(adminToken, companyId, name).path("data.id");
        return contentId != null ? contentId.longValue() : null;
    }

    // ===== Private 헬퍼 메서드 (현재 테스트 클래스 내부용) =====

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
        result.put("userId", response.path("data.id"));  // id 필드 사용
        return result;
    }

    /**
     * 업체 생성 후 ID 반환 (기본 메소드 재사용)
     */
    private Long 업체_생성_후_ID_반환(Long adminId, String name) {
        return CompanyAcceptanceTest.업체_생성_후_ID_반환(adminToken, adminId, name);
    }

    /**
     * 컨텐츠 생성 요청 (기본 메소드 재사용)
     */
    private ExtractableResponse<Response> 컨텐츠_생성_요청(String name) {
        return 컨텐츠_생성(adminToken, testCompanyId, name);
    }

    /**
     * 컨텐츠 생성 후 ID 반환 (기본 메소드 재사용)
     */
    private Long 컨텐츠_생성_후_ID_반환(String name) {
        return 컨텐츠_생성_후_ID_반환(adminToken, testCompanyId, name);
    }

    /**
     * 컨텐츠 조회 요청
     */
    private ExtractableResponse<Response> 컨텐츠_조회_요청(Long contentId) {
        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/api/content/" + contentId)
                .then()
                .extract();
    }

    /**
     * 컨텐츠 리스트 조회 요청
     */
    private ExtractableResponse<Response> 컨텐츠_리스트_조회_요청() {
        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/api/content")
                .then()
                .extract();
    }

    /**
     * 컨텐츠 찜 등록 요청
     */
    private ExtractableResponse<Response> 컨텐츠_찜_등록_요청(Long contentId) {
        Map<String, Object> request = new HashMap<>();
        request.put("contentId", contentId);

        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/content/favorite")
                .then()
                .extract();
    }

    /**
     * 컨텐츠 찜 삭제 요청
     */
    private ExtractableResponse<Response> 컨텐츠_찜_삭제_요청(Long contentId) {
        Map<String, Object> request = new HashMap<>();
        request.put("contentId", contentId);

        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .delete("/api/content/favorite")
                .then()
                .extract();
    }

    /**
     * 내 찜리스트 조회 요청
     */
    private ExtractableResponse<Response> 내_찜리스트_조회_요청() {
        return RestAssured.given()
                .log().all()
                .header("Authorization", "Bearer " + accessToken)
                .queryParam("page", 1)
                .queryParam("size", 10)
                .when()
                .get("/api/content/favorite")
                .then()
                .log().all()
                .extract();
    }

    // ===== 검증 메서드 (응답 확인) =====

    /**
     * 컨텐츠 생성 성공 확인
     */
    private void 컨텐츠_생성_성공_확인(ExtractableResponse<Response> response, String expectedName) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.id", notNullValue())
                .body("data.contentName", equalTo(expectedName))
                .body("data.company.companyId", equalTo(testCompanyId.intValue()))
                .body("data.minPeople", equalTo(2))
                .body("data.maxPeople", equalTo(6));
    }

    /**
     * 컨텐츠 조회 성공 확인
     */
    private void 컨텐츠_조회_성공_확인(ExtractableResponse<Response> response, Long expectedId, String expectedName) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.id", equalTo(expectedId.intValue()))
                .body("data.contentName", equalTo(expectedName))
                .body("data.company.companyId", notNullValue());
    }

    /**
     * 컨텐츠 리스트 조회 성공 확인
     */
    private void 컨텐츠_리스트_조회_성공_확인(ExtractableResponse<Response> response, int minSize) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.contents", notNullValue());

        // 최소 개수 확인
        Object data = response.path("data.contents");
        if (data instanceof List) {
            List<?> list = (List<?>) data;
            assert list.size() >= minSize : "Expected at least " + minSize + " contents, but got " + list.size();
        }
    }

    /**
     * 컨텐츠 찜 등록 성공 확인
     */
    private void 컨텐츠_찜_등록_성공_확인(ExtractableResponse<Response> response, Long expectedContentId) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.isFavorite", equalTo(true))
                .body("data.count", notNullValue());
    }

    /**
     * 컨텐츠 찜 삭제 성공 확인
     */
    private void 컨텐츠_찜_삭제_성공_확인(ExtractableResponse<Response> response) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200));
    }

    /**
     * 내 찜리스트 조회 성공 확인
     */
    private void 내_찜리스트_조회_성공_확인(ExtractableResponse<Response> response, int minSize) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.contents", notNullValue());

        // 최소 개수 확인
        Object data = response.path("data.contents");
        if (data instanceof List) {
            List<?> list = (List<?>) data;
            assert list.size() >= minSize : "Expected at least " + minSize + " favorites, but got " + list.size();
        }
    }
}
