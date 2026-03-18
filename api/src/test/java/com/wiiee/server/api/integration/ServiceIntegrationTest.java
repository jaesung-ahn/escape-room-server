package com.wiiee.server.api.integration;

import com.wiiee.server.api.domain.gathering.GatheringNotificationService;
import com.wiiee.server.api.infrastructure.aws.S3Util;
import com.wiiee.server.api.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public abstract class ServiceIntegrationTest {

    @MockBean
    protected S3Util s3Util;

    @MockBean
    protected GatheringNotificationService gatheringNotificationService;

    static PostgreSQLContainer<?> postgres;
    static GenericContainer<?> redis;

    static {
        postgres = new PostgreSQLContainer<>("postgres:12-alpine")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withUrlParam("sslmode", "disable");
        postgres.start();

        redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
                .withExposedPorts(6379);
        redis.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        // PostgreSQL
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.open-in-view", () -> "false");

        // Redis
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);

        // JWT
        registry.add("jwt.secret", () -> "dGVzdC1zZWNyZXQta2V5LWZvci1pbnRlZ3JhdGlvbi10ZXN0LW1pbi0zMi1ieXRlcw==");
        registry.add("jwt.access-token-expiration-ms", () -> "7200000");
        registry.add("jwt.refresh-token-expiration-ms", () -> "172800000");

        // CORS
        registry.add("cors.allowed-origins", () -> "http://localhost:3000");
        registry.add("cors.max-age", () -> "3600");

        // AWS S3
        registry.add("cloud.aws.credentials.accessKey", () -> "test-key");
        registry.add("cloud.aws.credentials.secretKey", () -> "test-secret");
        registry.add("cloud.aws.s3.bucket", () -> "test-bucket");
        registry.add("cloud.aws.region.static", () -> "ap-northeast-2");

        // Disable optional features
        registry.add("rate-limiter.enabled", () -> "false");
        registry.add("slack.enabled", () -> "false");
        registry.add("slack.token", () -> "mock-token");
        registry.add("slack.channel.monitor", () -> "#test");
        registry.add("push.enabled", () -> "false");
        registry.add("push.api.url", () -> "http://localhost:9999");
    }

    @Autowired
    protected TransactionTemplate transactionTemplate;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void cleanUp() {
        databaseCleanup.afterPropertiesSet();
        databaseCleanup.execute();
    }
}
