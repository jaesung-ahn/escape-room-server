package com.wiiee.server.api.infrastructure.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.wiiee.server.api.config.properties.AwsS3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class S3Configuration {

    private final AwsS3Properties awsS3Properties;

    @Bean
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(
                awsS3Properties.credentials().accessKey(),
                awsS3Properties.credentials().secretKey());
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(awsS3Properties.region().staticRegion())
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}
