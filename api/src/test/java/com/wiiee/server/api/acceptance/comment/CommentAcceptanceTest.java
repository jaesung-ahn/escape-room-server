package com.wiiee.server.api.acceptance.comment;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;

@DisplayName("Comment API 인수 테스트")
class CommentAcceptanceTest extends AcceptanceTest {

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
    private Long testGatheringId;
    private String accessToken;  // 일반 USER 토큰
    private String adminToken;   // ADMIN 토큰

    @BeforeEach
    void setUpData() {
        // 1. AdminUser 생성
        AdminUser admin = AdminUser.of("comment_test_admin@wiiee.com", "admin123!");
        Long adminId = adminRepository.save(admin).getId();

        // 2. ADMIN 역할 사용자 생성 및 토큰 발급
        Password adminPassword = Password.of("password123!", passwordEncoder);
        User adminUser = User.ofWithRole("admin@example.com", "adminUser", adminPassword, UserRole.ADMIN);
        User savedAdminUser = userRepository.save(adminUser);
        adminToken = jwtTokenProvider.createToken(savedAdminUser.getEmail()).getAccessToken();

        // 3. Company 생성 (ADMIN 권한 필요)
        testCompanyId = CompanyAcceptanceTest.업체_생성_후_ID_반환(adminToken, adminId, "댓글 테스트 방탈출");

        // 4. User 생성 및 토큰 발급
        Map<String, Object> userResponse = UserAcceptanceTest.회원가입_후_응답_반환("comment_user@example.com", "댓글테스터", "pass123!");
        accessToken = (String) userResponse.get("accessToken");

        // 5. Content 생성 (ADMIN 권한 필요)
        testContentId = ContentAcceptanceTest.컨텐츠_생성_후_ID_반환(adminToken, testCompanyId, "댓글 테스트용 방탈출");

        // 6. Gathering 생성
        testGatheringId = GatheringAcceptanceTest.동행_모집_등록_후_ID_반환(accessToken, testContentId, "댓글 테스트용 동행", "댓글 달아주세요");
    }

    // ===== 테스트 케이스 =====

    @Test
    @DisplayName("댓글 등록")
    void createComment() {
        // given: 댓글 내용
        String message = "함께 방탈출 하고 싶어요!";

        // when: 댓글 등록 요청
        ExtractableResponse<Response> response = 댓글_등록_요청(testGatheringId, message);

        // then: 성공 응답
        댓글_등록_성공_확인(response, message);
    }

    @Test
    @DisplayName("댓글 목록 조회")
    void getComments() {
        // given: 여러 댓글 생성
        댓글_등록_요청(testGatheringId, "첫 번째 댓글");
        댓글_등록_요청(testGatheringId, "두 번째 댓글");
        댓글_등록_요청(testGatheringId, "세 번째 댓글");

        // when: 댓글 목록 조회
        ExtractableResponse<Response> response = 댓글_목록_조회_요청(testGatheringId);

        // then: 성공 응답 (최소 3개 이상)
        댓글_목록_조회_성공_확인(response, 3);
    }

    @Test
    @DisplayName("댓글 수정")
    void updateComment() {
        // given: 기존 댓글 생성
        String originalMessage = "처음 작성한 댓글";
        Long commentId = 댓글_등록_후_ID_반환(testGatheringId, originalMessage);

        // when: 댓글 수정 요청
        String updatedMessage = "수정된 댓글 내용";
        ExtractableResponse<Response> response = 댓글_수정_요청(commentId, updatedMessage);

        // then: 성공 응답
        댓글_수정_성공_확인(response, updatedMessage);
    }

    @Test
    @DisplayName("댓글 삭제")
    void deleteComment() {
        // given: 기존 댓글 생성
        String message = "삭제할 댓글";
        Long commentId = 댓글_등록_후_ID_반환(testGatheringId, message);

        // when: 댓글 삭제 요청
        ExtractableResponse<Response> response = 댓글_삭제_요청(commentId);

        // then: 성공 응답
        댓글_삭제_성공_확인(response);
    }

    @Test
    @DisplayName("대댓글 등록")
    void createReplyComment() {
        // given: 부모 댓글 생성
        String parentMessage = "부모 댓글입니다";
        Long parentCommentId = 댓글_등록_후_ID_반환(testGatheringId, parentMessage);

        // when: 대댓글 등록 요청
        String replyMessage = "대댓글입니다";
        ExtractableResponse<Response> response = 대댓글_등록_요청(testGatheringId, parentCommentId, replyMessage);

        // then: 성공 응답
        댓글_등록_성공_확인(response, replyMessage);
    }

    @Test
    @DisplayName("다른 사용자의 댓글 수정 실패")
    void updateComment_notOwner() {
        // given: 다른 사용자 생성 및 댓글 작성
        Map<String, Object> anotherUserResponse = UserAcceptanceTest.회원가입_후_응답_반환("another_user@example.com", "다른사용자", "pass123!");
        String anotherToken = (String) anotherUserResponse.get("accessToken");
        Long commentId = 댓글_등록_후_ID_반환_with_token(testGatheringId, "다른 사용자의 댓글", anotherToken);

        // when: 내가 다른 사용자의 댓글 수정 시도
        ExtractableResponse<Response> response = 댓글_수정_요청(commentId, "수정 시도");

        // then: 실패 응답 (400 또는 403)
        response.response()
                .then()
                .statusCode(anyOf(equalTo(HttpStatus.BAD_REQUEST.value()), equalTo(HttpStatus.FORBIDDEN.value())));
    }

    @Test
    @DisplayName("다른 사용자의 댓글 삭제 실패")
    void deleteComment_notOwner() {
        // given: 다른 사용자 생성 및 댓글 작성
        Map<String, Object> anotherUserResponse = UserAcceptanceTest.회원가입_후_응답_반환("delete_test@example.com", "삭제테스트", "pass123!");
        String anotherToken = (String) anotherUserResponse.get("accessToken");
        Long commentId = 댓글_등록_후_ID_반환_with_token(testGatheringId, "다른 사용자의 댓글", anotherToken);

        // when: 내가 다른 사용자의 댓글 삭제 시도
        ExtractableResponse<Response> response = 댓글_삭제_요청(commentId);

        // then: 권한 없음 에러 (7001)
        response.response()
                .then()
                .body("code", equalTo(7001))
                .body("message", equalTo("댓글의 작성자가 아니면 할 수 없는 권한입니다."));
    }

    @Test
    @DisplayName("댓글 등록 - 인증 없이 요청 시 실패")
    void createComment_unauthorized() {
        // given: 댓글 내용
        Map<String, Object> request = new HashMap<>();
        request.put("message", "인증 없는 댓글");

        // when: 인증 없이 댓글 등록
        ExtractableResponse<Response> response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/comment/gathering/" + testGatheringId)
                .then()
                .extract();

        // then: 401 Unauthorized 또는 403 Forbidden
        response.response()
                .then()
                .statusCode(anyOf(equalTo(HttpStatus.UNAUTHORIZED.value()), equalTo(HttpStatus.FORBIDDEN.value())));
    }

    @Test
    @DisplayName("존재하지 않는 동행에 댓글 등록 실패")
    void createComment_gatheringNotFound() {
        // given: 존재하지 않는 동행 ID
        Long invalidGatheringId = 999999L;
        String message = "존재하지 않는 동행의 댓글";

        // when: 댓글 등록 요청
        ExtractableResponse<Response> response = 댓글_등록_요청(invalidGatheringId, message);

        // then: Gathering 없음 에러 (7002)
        response.response()
                .then()
                .body("code", equalTo(7002))
                .body("message", equalTo("존재하지 않는 동행 모집입니다."));
    }

    @Test
    @DisplayName("빈 메시지로 댓글 등록 실패")
    void createComment_emptyMessage() {
        // given: 빈 메시지
        String emptyMessage = "";

        // when: 댓글 등록 요청
        ExtractableResponse<Response> response = 댓글_등록_요청(testGatheringId, emptyMessage);

        // then: Validation 에러 (400)
        response.response()
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("code", equalTo(400))
                .body("message", equalTo("메세지를 입력하세요."));
    }

    @Test
    @DisplayName("대댓글이 포함된 댓글 목록 조회")
    void getCommentsWithReplies() {
        // given: 부모 댓글과 대댓글들 생성
        String parentMessage = "부모 댓글입니다";
        Long parentCommentId = 댓글_등록_후_ID_반환(testGatheringId, parentMessage);

        대댓글_등록_요청(testGatheringId, parentCommentId, "첫 번째 대댓글");
        대댓글_등록_요청(testGatheringId, parentCommentId, "두 번째 대댓글");

        // when: 댓글 목록 조회
        ExtractableResponse<Response> response = 댓글_목록_조회_요청(testGatheringId);

        // then: 부모 댓글에 대댓글이 포함됨
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.comments", notNullValue())
                .body("data.comments[0].children", notNullValue())
                .body("data.comments[0].children.size()", greaterThanOrEqualTo(2))
                .body("data.comments[0].isParent", equalTo(true))
                .body("data.comments[0].children[0].isParent", equalTo(false));
    }

    @Test
    @DisplayName("삭제된 댓글 조회 - Soft Delete 확인")
    void getComments_afterDelete() {
        // given: 댓글 생성 후 삭제
        String message = "삭제될 댓글";
        Long commentId = 댓글_등록_후_ID_반환(testGatheringId, message);
        댓글_삭제_요청(commentId);

        // when: 댓글 목록 조회
        ExtractableResponse<Response> response = 댓글_목록_조회_요청(testGatheringId);

        // then: 삭제된 댓글은 deleted=true 상태로 조회됨
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.comments", notNullValue());

        // 삭제된 댓글 확인 (deleted 필드가 true인지)
        List<Map<String, Object>> comments = response.path("data.comments");
        boolean hasDeletedComment = comments.stream()
                .anyMatch(comment -> {
                    Boolean deleted = (Boolean) comment.get("deleted");
                    return deleted != null && deleted;
                });

        assert hasDeletedComment : "삭제된 댓글이 목록에 포함되어야 합니다";
    }

    @Test
    @DisplayName("존재하지 않는 댓글 수정 실패")
    void updateComment_notFound() {
        // given: 존재하지 않는 댓글 ID
        Long invalidCommentId = 999999L;
        String message = "수정할 내용";

        // when: 존재하지 않는 댓글 수정 시도
        ExtractableResponse<Response> response = 댓글_수정_요청(invalidCommentId, message);

        // then: 실패 응답 (400 또는 404)
        response.response()
                .then()
                .statusCode(anyOf(equalTo(HttpStatus.BAD_REQUEST.value()), equalTo(HttpStatus.NOT_FOUND.value())));
    }

    @Test
    @DisplayName("존재하지 않는 댓글 삭제 실패")
    void deleteComment_notFound() {
        // given: 존재하지 않는 댓글 ID
        Long invalidCommentId = 999999L;

        // when: 존재하지 않는 댓글 삭제 시도
        ExtractableResponse<Response> response = 댓글_삭제_요청(invalidCommentId);

        // then: 실패 응답 (400 또는 404)
        response.response()
                .then()
                .statusCode(anyOf(equalTo(HttpStatus.BAD_REQUEST.value()), equalTo(HttpStatus.NOT_FOUND.value())));
    }

    // ===== 헬퍼 메서드 (HTTP 요청) =====

    /**
     * 댓글 등록 요청
     */
    private ExtractableResponse<Response> 댓글_등록_요청(Long gatheringId, String message) {
        Map<String, Object> request = new HashMap<>();
        request.put("message", message);

        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/comment/gathering/" + gatheringId)
                .then()
                .extract();
    }

    /**
     * 댓글 등록 후 ID 반환
     */
    private Long 댓글_등록_후_ID_반환(Long gatheringId, String message) {
        Number commentId = 댓글_등록_요청(gatheringId, message).path("data.id");
        return commentId != null ? commentId.longValue() : null;
    }

    /**
     * 댓글 등록 후 ID 반환 (특정 토큰 사용)
     */
    private Long 댓글_등록_후_ID_반환_with_token(Long gatheringId, String message, String token) {
        Map<String, Object> request = new HashMap<>();
        request.put("message", message);

        Number commentId = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/comment/gathering/" + gatheringId)
                .then()
                .extract()
                .path("data.id");

        return commentId != null ? commentId.longValue() : null;
    }

    /**
     * 대댓글 등록 요청
     */
    private ExtractableResponse<Response> 대댓글_등록_요청(Long gatheringId, Long parentCommentId, String message) {
        Map<String, Object> request = new HashMap<>();
        request.put("parentCommentId", parentCommentId);
        request.put("message", message);

        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/comment/gathering/" + gatheringId)
                .then()
                .extract();
    }

    /**
     * 댓글 목록 조회 요청
     */
    private ExtractableResponse<Response> 댓글_목록_조회_요청(Long gatheringId) {
        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/api/comment/gathering/" + gatheringId)
                .then()
                .extract();
    }

    /**
     * 댓글 수정 요청
     */
    private ExtractableResponse<Response> 댓글_수정_요청(Long commentId, String message) {
        Map<String, Object> request = new HashMap<>();
        request.put("message", message);

        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/api/comment/" + commentId)
                .then()
                .extract();
    }

    /**
     * 댓글 삭제 요청
     */
    private ExtractableResponse<Response> 댓글_삭제_요청(Long commentId) {
        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete("/api/comment/" + commentId)
                .then()
                .extract();
    }

    // ===== 검증 메서드 (응답 확인) =====

    /**
     * 댓글 등록 성공 확인
     */
    private void 댓글_등록_성공_확인(ExtractableResponse<Response> response, String expectedMessage) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.id", notNullValue())
                .body("data.message", equalTo(expectedMessage))
                .body("data.isOwner", equalTo(true))
                .body("data.writer", notNullValue());
    }

    /**
     * 댓글 목록 조회 성공 확인
     */
    private void 댓글_목록_조회_성공_확인(ExtractableResponse<Response> response, int minCount) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.comments", notNullValue())
                .body("data.count", greaterThanOrEqualTo(minCount));
    }

    /**
     * 댓글 수정 성공 확인
     */
    private void 댓글_수정_성공_확인(ExtractableResponse<Response> response, String expectedMessage) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.message", equalTo(expectedMessage));
    }

    /**
     * 댓글 삭제 성공 확인
     */
    private void 댓글_삭제_성공_확인(ExtractableResponse<Response> response) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200));
    }
}
