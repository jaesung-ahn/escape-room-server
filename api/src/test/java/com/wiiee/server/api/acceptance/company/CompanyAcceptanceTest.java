package com.wiiee.server.api.acceptance.company;

import com.wiiee.server.api.AcceptanceTest;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;

@DisplayName("Company API 인수 테스트")
public class CompanyAcceptanceTest extends AcceptanceTest {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long testAdminId;
    private String adminToken;  // ADMIN 권한 토큰

    @BeforeEach
    void setUpAdmin() {
        // AcceptanceTest의 setUp이 먼저 실행되어 DB 정리됨
        // AdminUser 생성
        AdminUser admin = AdminUser.of("test_admin@wiiee.com", "admin123!");
        AdminUser savedAdmin = adminRepository.save(admin);
        testAdminId = savedAdmin.getId();

        // ADMIN 역할 사용자 생성 및 토큰 발급
        Password adminPassword = Password.of("password123!", passwordEncoder);
        User adminUser = User.ofWithRole("admin@example.com", "adminUser", adminPassword, UserRole.ADMIN);
        User savedAdminUser = userRepository.save(adminUser);
        adminToken = jwtTokenProvider.createToken(savedAdminUser.getEmail()).getAccessToken();
    }

    // ===== 테스트 케이스 =====

    @Test
    @DisplayName("업체 생성")
    void createCompany() {
        // given: 업체 정보
        String companyName = "위이 방탈출";

        // when: 업체 생성 요청
        ExtractableResponse<Response> response = 업체_생성_요청(companyName);

        // then: 성공 응답
        업체_생성_성공_확인(response, companyName);
    }

    @Test
    @DisplayName("업체 상세 조회")
    void getCompany() {
        // given: 기존 업체 생성
        String companyName = "조회용 업체";
        Long companyId = 업체_생성_후_ID_반환(companyName);

        // 회원가입 후 토큰 획득 (인증 필요)
        String accessToken = 회원가입_후_토큰_발급();

        // when: 업체 상세 조회
        ExtractableResponse<Response> response = 업체_조회_요청(accessToken, companyId);

        // then: 성공 응답
        업체_조회_성공_확인(response, companyName);
    }

    @Test
    @DisplayName("업체 상세 조회 - 인증 없이 요청 시 실패")
    void getCompany_unauthorized() {
        // given: 기존 업체 생성
        Long companyId = 업체_생성_후_ID_반환("테스트 업체");

        // when: 인증 없이 업체 조회
        ExtractableResponse<Response> response = RestAssured.given()
                .when()
                .get("/api/company/" + companyId)
                .then()
                .extract();

        // then: 401 Unauthorized 또는 403 Forbidden
        response.response()
                .then()
                .statusCode(anyOf(equalTo(HttpStatus.UNAUTHORIZED.value()), equalTo(HttpStatus.FORBIDDEN.value())));
    }

    @Test
    @DisplayName("업체 리스트 조회")
    void getCompanies() {
        // given: 여러 업체 생성
        업체_생성_요청("업체1");
        업체_생성_요청("업체2");
        업체_생성_요청("업체3");

        // 회원가입 후 토큰 획득 (인증 필요)
        String accessToken = 회원가입_후_토큰_발급();

        // when: 업체 리스트 조회
        ExtractableResponse<Response> response = 업체_리스트_조회_요청(accessToken);

        // then: 성공 응답 (최소 3개 이상)
        업체_리스트_조회_성공_확인(response, 3);
    }

    // ===== Public Static 헬퍼 메서드 (다른 테스트에서 사용) =====

    /**
     * 업체 생성 (기본 메소드)
     * 모든 업체 생성 관련 메소드의 기반이 되는 메소드
     *
     * @param adminToken ADMIN 권한 토큰
     * @param adminId AdminUser ID
     * @param name 업체명
     * @return ExtractableResponse - 필요한 정보를 자유롭게 추출 가능
     */
    public static ExtractableResponse<Response> 업체_생성(String adminToken, Long adminId, String name) {
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

        return RestAssured.given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/company")
                .then()
                .extract();
    }

    /**
     * 업체 생성 후 ID 반환
     */
    public static Long 업체_생성_후_ID_반환(String adminToken, Long adminId, String name) {
        Number companyId = 업체_생성(adminToken, adminId, name).path("data.companyId");
        return companyId != null ? companyId.longValue() : null;
    }

    // ===== Private 헬퍼 메서드 (현재 테스트 클래스 내부용) =====

    /**
     * 회원가입 후 토큰 발급
     */
    private String 회원가입_후_토큰_발급() {
        Map<String, Object> request = new HashMap<>();
        request.put("email", "company_test@example.com");
        request.put("nickname", "업체테스터");
        request.put("password", "password123!");

        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/user")
                .then()
                .extract()
                .path("data.accessToken");
    }

    /**
     * 업체 생성 요청 (기본 메소드 재사용)
     */
    private ExtractableResponse<Response> 업체_생성_요청(String name) {
        return 업체_생성(adminToken, testAdminId, name);
    }

    /**
     * 업체 생성 후 ID 반환 (기본 메소드 재사용)
     */
    private Long 업체_생성_후_ID_반환(String name) {
        return 업체_생성_후_ID_반환(adminToken, testAdminId, name);
    }

    /**
     * 업체 조회 요청
     */
    private ExtractableResponse<Response> 업체_조회_요청(String accessToken, Long companyId) {
        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/api/company/" + companyId)
                .then()
                .extract();
    }

    /**
     * 업체 리스트 조회 요청
     */
    private ExtractableResponse<Response> 업체_리스트_조회_요청(String accessToken) {
        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/api/company")
                .then()
                .extract();
    }

    // ===== 검증 메서드 (응답 확인) =====

    /**
     * 업체 생성 성공 확인
     */
    private void 업체_생성_성공_확인(ExtractableResponse<Response> response, String expectedName) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.companyId", notNullValue())
                .body("data.name", equalTo(expectedName))
                .body("data.state", equalTo("서울특별시"))
                .body("data.city", equalTo("강남구"))
                .body("data.contact", equalTo("02-1234-5678"))
                .body("data.url", equalTo("https://test.com"));
    }

    /**
     * 업체 조회 성공 확인
     */
    private void 업체_조회_성공_확인(ExtractableResponse<Response> response, String expectedName) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.companyId", notNullValue())
                .body("data.name", equalTo(expectedName))
                .body("data.state", notNullValue())
                .body("data.city", notNullValue())
                .body("data.contact", notNullValue());
    }

    /**
     * 업체 리스트 조회 성공 확인
     */
    private void 업체_리스트_조회_성공_확인(ExtractableResponse<Response> response, int minSize) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data", notNullValue());

        // 최소 개수 확인
        Object data = response.path("data");
        if (data instanceof List) {
            List<?> list = (List<?>) data;
            assert list.size() >= minSize : "Expected at least " + minSize + " companies, but got " + list.size();
        }
    }
}