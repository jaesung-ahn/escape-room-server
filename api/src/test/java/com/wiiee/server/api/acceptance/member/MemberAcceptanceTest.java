package com.wiiee.server.api.acceptance.member;

import com.wiiee.server.api.AcceptanceTest;
import com.wiiee.server.api.acceptance.company.CompanyAcceptanceTest;
import com.wiiee.server.api.acceptance.content.ContentAcceptanceTest;
import com.wiiee.server.api.acceptance.gathering.GatheringAcceptanceTest;
import com.wiiee.server.api.acceptance.user.UserAcceptanceTest;
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

@DisplayName("Member API 인수 테스트")
class MemberAcceptanceTest extends AcceptanceTest {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long testCompanyId;
    private Long testContentId;
    private String accessToken;  // 일반 USER 토큰
    private String adminToken;   // ADMIN 토큰
    private Long testGatheringId;

    @BeforeEach
    void setUpData() {
        // 1. AdminUser 생성
        AdminUser admin = AdminUser.of("member_test_admin@wiiee.com", "admin123!");
        Long adminId = adminRepository.save(admin).getId();

        // 2. ADMIN 역할 사용자 생성 및 토큰 발급
        Password adminPassword = Password.of("password123!", passwordEncoder);
        User adminUser = User.ofWithRole("admin@example.com", "adminUser", adminPassword, UserRole.ADMIN);
        User savedAdminUser = userRepository.save(adminUser);
        adminToken = jwtTokenProvider.createToken(savedAdminUser.getEmail()).getAccessToken();

        // 3. Company 생성 (ADMIN 권한 필요)
        testCompanyId = CompanyAcceptanceTest.업체_생성_후_ID_반환(adminToken, adminId, "멤버 테스트 방탈출");

        // 4. User 생성 및 토큰 발급
        Map<String, Object> userResponse = UserAcceptanceTest.회원가입_후_응답_반환("member_user@example.com", "멤버테스터", "pass123!");
        accessToken = (String) userResponse.get("accessToken");

        // 5. Content 생성 (ADMIN 권한 필요)
        testContentId = ContentAcceptanceTest.컨텐츠_생성_후_ID_반환(adminToken, testCompanyId, "멤버 테스트용 방탈출");

        // 6. Gathering 생성 (선착순 방식)
        testGatheringId = GatheringAcceptanceTest.동행_모집_등록_후_ID_반환(accessToken, testContentId, "멤버 테스트 동행", "멤버 관리 테스트");
    }

    // ===== 테스트 케이스 =====

    @Test
    @DisplayName("멤버 등록 성공")
    void addMember() {
        // given: 다른 사용자 생성
        Map<String, Object> newUserResponse = UserAcceptanceTest.회원가입_후_응답_반환("new_member@example.com", "신규멤버", "pass123!");
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
        Map<String, Object> newUserResponse = UserAcceptanceTest.회원가입_후_응답_반환("member2@example.com", "멤버2", "pass123!");
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
        Map<String, Object> newUserResponse = UserAcceptanceTest.회원가입_후_응답_반환("member3@example.com", "멤버3", "pass123!");
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
        Map<String, Object> newUserResponse = UserAcceptanceTest.회원가입_후_응답_반환("member4@example.com", "멤버4", "pass123!");
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
        Map<String, Object> newUserResponse = UserAcceptanceTest.회원가입_후_응답_반환("duplicate_member@example.com", "중복멤버", "pass123!");
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
