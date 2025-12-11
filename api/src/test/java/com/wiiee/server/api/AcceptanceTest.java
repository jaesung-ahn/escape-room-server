package com.wiiee.server.api;

import com.wiiee.server.api.infrastructure.aws.S3Util;
import com.wiiee.server.api.utils.DatabaseCleanup;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest {

    @MockBean
    protected S3Util s3Util;

    // Singleton 패턴: static 초기화 블록으로 JVM 전체에서 단 한 번만 생성
    // @Container 방식은 IntelliJ와 Gradle에서 동작이 다를 수 있어 안정성 문제 발생
    // 수동으로 컨테이너를 시작하여 모든 환경에서 일관된 동작 보장
    static PostgreSQLContainer<?> postgres;

    static {
        postgres = new PostgreSQLContainer<>("postgres:12-alpine")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withUrlParam("sslmode", "disable");
        postgres.start();
    }

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleanup databaseCleanup;

    @BeforeEach
    protected void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();  // 실패 시 로깅
            databaseCleanup.afterPropertiesSet();
        }

        given(s3Util.upload(anyString(), any()))
                .willReturn("https://s3.amazonaws.com/test-bucket/test-image.jpg");

        databaseCleanup.execute();
    }

}
