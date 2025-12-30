package com.wiiee.server.api.acceptance.security;

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

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("RBAC 인수 테스트")
class RbacAcceptanceTest extends AcceptanceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long testAdminId;
    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUpRbac() {
        // AdminUser 생성 (업체 생성 시 필요)
        AdminUser admin = AdminUser.of("test_admin@wiiee.com", "admin123!");
        AdminUser savedAdmin = adminRepository.save(admin);
        testAdminId = savedAdmin.getId();

        // USER 역할 사용자 생성 및 토큰 발급
        Password userPassword = Password.of("password123!", passwordEncoder);
        User normalUser = User.ofWithRole("user@example.com", "testUser", userPassword, UserRole.USER);
        User savedUser = userRepository.save(normalUser);
        userToken = jwtTokenProvider.createToken(savedUser.getEmail()).getAccessToken();

        // ADMIN 역할 사용자 생성 및 토큰 발급
        Password adminPassword = Password.of("password123!", passwordEncoder);
        User adminUser = User.ofWithRole("admin@example.com", "adminUser", adminPassword, UserRole.ADMIN);
        User savedAdminUser = userRepository.save(adminUser);
        adminToken = jwtTokenProvider.createToken(savedAdminUser.getEmail()).getAccessToken();
    }

    @Test
    @DisplayName("USER 역할로 ADMIN 전용 API 호출 시 403 Forbidden")
    void user_cannot_access_admin_api() {
        // given: USER 역할 토큰

        // when: 업체 생성 API 호출 (ADMIN 전용)
        ExtractableResponse<Response> response = 업체_생성_요청(userToken, "테스트 업체");

        // Debug: 응답 바디 출력
        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body().asString());

        // then: 403 Forbidden
        response.response()
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("ADMIN 역할로 ADMIN 전용 API 호출 시 성공")
    void admin_can_access_admin_api() {
        // given: ADMIN 역할 토큰

        // when: 업체 생성 API 호출 (ADMIN 전용)
        ExtractableResponse<Response> response = 업체_생성_요청(adminToken, "관리자 업체");

        // then: 200 OK
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.companyId", notNullValue())
                .body("data.name", equalTo("관리자 업체"));
    }

    @Test
    @DisplayName("USER 역할로 Content 생성 API 호출 시 403 Forbidden")
    void user_cannot_create_content() {
        // given: 업체 먼저 생성
        Long companyId = 업체_생성_후_ID_반환(adminToken, "테스트 업체");

        // when: 컨텐츠 생성 API 호출 (ADMIN 전용)
        ExtractableResponse<Response> response = 컨텐츠_생성_요청(userToken, companyId, "테스트 컨텐츠");

        // then: 403 Forbidden
        response.response()
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("ADMIN 역할로 Content 생성 API 호출 시 성공")
    void admin_can_create_content() {
        // given: 업체 먼저 생성
        Long companyId = 업체_생성_후_ID_반환(adminToken, "테스트 업체");

        // when: 컨텐츠 생성 API 호출 (ADMIN 전용)
        ExtractableResponse<Response> response = 컨텐츠_생성_요청(adminToken, companyId, "관리자 컨텐츠");

        // Debug: 응답 바디 출력
        System.out.println("Content Creation - Status Code: " + response.statusCode());
        System.out.println("Content Creation - Response Body: " + response.body().asString());

        // then: 200 OK
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.id", notNullValue())
                .body("data.contentName", equalTo("관리자 컨텐츠"));
    }

    @Test
    @DisplayName("인증 없이 ADMIN 전용 API 호출 시 401 Unauthorized")
    void no_auth_cannot_access_admin_api() {
        // when: 인증 없이 업체 생성 API 호출
        ExtractableResponse<Response> response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(업체_생성_요청_바디("테스트 업체"))
                .when()
                .post("/api/company")
                .then()
                .extract();

        // then: 401 Unauthorized
        response.response()
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    // ===== 헬퍼 메서드 =====

    private ExtractableResponse<Response> 업체_생성_요청(String token, String name) {
        return RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(업체_생성_요청_바디(name))
                .when()
                .post("/api/company")
                .then()
                .extract();
    }

    private Map<String, Object> 업체_생성_요청_바디(String name) {
        Map<String, Object> request = new HashMap<>();
        request.put("adminId", testAdminId);
        request.put("name", name);
        request.put("stateCode", 1);
        request.put("cityCode", 1);
        request.put("address", "테헤란로 123");
        request.put("detailAddress", "2층");
        request.put("notice", "영업 중입니다");
        request.put("contact", "02-1234-5678");
        request.put("url", "https://wiiee.com");
        request.put("isOperated", true);
        request.put("businessDayCodes", List.of(1, 2, 3, 4, 5, 6, 7));
        request.put("isAlwaysOperated", true);
        request.put("imageIds", List.of());
        request.put("registrationImageId", 0L);
        request.put("businessNumber", "123-45-67890");
        request.put("representativeName", "홍길동");
        request.put("repContractNumber", "010-1234-5678");
        request.put("chargeContractNumber", "010-9876-5432");
        request.put("bankCode", 1);
        request.put("account", "1234567890");
        return request;
    }

    private Long 업체_생성_후_ID_반환(String token, String name) {
        ExtractableResponse<Response> response = 업체_생성_요청(token, name);
        Number companyId = response.path("data.companyId");
        return companyId != null ? companyId.longValue() : null;
    }

    private ExtractableResponse<Response> 컨텐츠_생성_요청(String token, Long companyId, String title) {
        Map<String, Object> request = new HashMap<>();
        request.put("companyId", companyId);
        request.put("name", title);
        request.put("genreCode", 1);
        request.put("imageIds", List.of());
        request.put("minPeople", 2);
        request.put("maxPeople", 4);
        request.put("information", "재미있는 방탈출");
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
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/content")
                .then()
                .extract();
    }
}
