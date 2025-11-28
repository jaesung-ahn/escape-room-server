package com.wiiee.server.api.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.findify.s3mock.S3Mock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Slf4j
@Configuration
@Profile({"local", "!test"})  // Only active in local, not in test
public class S3MockConfig {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    private S3Mock s3Mock;

    @PostConstruct
    public void startS3Mock() {
        s3Mock = new S3Mock.Builder()
                .withPort(8001)
                .withInMemoryBackend()
                .build();
        s3Mock.start();
        log.info("S3Mock started on port 8001");
    }

    @PreDestroy
    public void stopS3Mock() {
        if (s3Mock != null) {
            s3Mock.stop();
            log.info("S3Mock stopped");
        }
    }

    @Bean
    public AmazonS3 amazonS3() {
        AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration(
                "http://localhost:8001",
                "ap-northeast-2"
        );

        AmazonS3 client = AmazonS3ClientBuilder
                .standard()
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(endpoint)
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(accessKey, secretKey)))
                .build();

        // Create bucket if not exists
        if (!client.doesBucketExistV2(bucket)) {
            client.createBucket(bucket);
            log.info("Created S3 mock bucket: {}", bucket);
        }

        return client;
    }
}
