package com.wiiee.server.api.acceptance.image;

import com.wiiee.server.api.AcceptanceTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.io.File;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("Image API 인수 테스트")
class ImageAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("URL로 이미지 등록")
    void createImageByURL() {
        // given: 이미지 URL
        String imageUrl = "https://example.com/test-image.jpg";

        // when: URL로 이미지 등록
        ExtractableResponse<Response> response = URL로_이미지_등록_요청(imageUrl);

        // then: 성공 응답과 이미지 정보 반환
        이미지_등록_성공_확인(response, imageUrl);
    }

    @Test
    @DisplayName("이미지 조회")
    void getImage() {
        // given: 기존 이미지 등록
        String imageUrl = "https://example.com/test-image-for-get.jpg";
        Long imageId = URL로_이미지_등록_후_ID_반환(imageUrl);

        // when: 이미지 조회
        ExtractableResponse<Response> response = 이미지_조회_요청(imageId);

        // then: 성공 응답과 이미지 정보 반환
        이미지_조회_성공_확인(response, imageId, imageUrl);
    }

    @Test
    @DisplayName("존재하지 않는 이미지 조회")
    void getImage_notFound() {
        // given: 존재하지 않는 이미지 ID
        Long nonExistentId = 999999L;

        // when: 이미지 조회
        ExtractableResponse<Response> response = 이미지_조회_요청(nonExistentId);

        // then: 빈 URL로 응답 (getImageById는 없을 때 빈 Image("") 반환)
        이미지_조회_빈_URL_확인(response);
    }

    @Test
    @DisplayName("파일로 이미지 업로드")
    void uploadImageFile() {
        // given: 테스트용 이미지 파일 (간단한 텍스트 파일로 대체)
        File testFile = 테스트_파일_생성();

        // when: 파일 업로드
        ExtractableResponse<Response> response = 파일로_이미지_업로드_요청(testFile);

        // then: 성공 응답과 이미지 정보 반환
        파일_업로드_성공_확인(response);

        // cleanup
        testFile.delete();
    }

    // ===== 헬퍼 메서드 =====

    /**
     * URL로 이미지 등록 요청
     */
    private ExtractableResponse<Response> URL로_이미지_등록_요청(String url) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .queryParam("url", url)
                .when()
                .post("/api/image/url")
                .then()
                .extract();
    }

    /**
     * URL로 이미지 등록 후 ID 반환
     */
    private Long URL로_이미지_등록_후_ID_반환(String url) {
        Integer id = URL로_이미지_등록_요청(url)
                .path("data.id");
        return id.longValue();
    }

    /**
     * 이미지 조회 요청
     */
    private ExtractableResponse<Response> 이미지_조회_요청(Long imageId) {
        return RestAssured.given()
                .when()
                .get("/api/image/" + imageId)
                .then()
                .extract();
    }

    /**
     * 파일로 이미지 업로드 요청
     */
    private ExtractableResponse<Response> 파일로_이미지_업로드_요청(File file) {
        return RestAssured.given()
                .contentType(ContentType.MULTIPART)
                .multiPart("imageFile", file, "image/jpeg")
                .when()
                .post("/api/image")
                .then()
                .extract();
    }

    /**
     * 테스트용 파일 생성
     */
    private File 테스트_파일_생성() {
        try {
            File tempFile = File.createTempFile("test-image", ".jpg");
            tempFile.deleteOnExit();
            return tempFile;
        } catch (Exception e) {
            throw new RuntimeException("테스트 파일 생성 실패", e);
        }
    }

    // ===== 검증 메서드 =====

    /**
     * 이미지 등록 성공 확인
     */
    private void 이미지_등록_성공_확인(ExtractableResponse<Response> response, String expectedUrl) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.id", notNullValue())
                .body("data.url", equalTo(expectedUrl));
    }

    /**
     * 이미지 조회 성공 확인
     */
    private void 이미지_조회_성공_확인(ExtractableResponse<Response> response, Long expectedId, String expectedUrl) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.id", equalTo(expectedId.intValue()))
                .body("data.url", equalTo(expectedUrl));
    }

    /**
     * 이미지 조회 빈 URL 확인 (존재하지 않는 이미지)
     */
    private void 이미지_조회_빈_URL_확인(ExtractableResponse<Response> response) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.url", equalTo(""));
    }

    /**
     * 파일 업로드 성공 확인
     */
    private void 파일_업로드_성공_확인(ExtractableResponse<Response> response) {
        response.response()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("code", equalTo(200))
                .body("data.id", notNullValue())
                .body("data.url", notNullValue());
    }
}
