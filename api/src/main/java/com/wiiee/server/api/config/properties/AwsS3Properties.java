package com.wiiee.server.api.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;

@ConfigurationProperties(prefix = "cloud.aws")
public record AwsS3Properties(
        Credentials credentials,
        Region region,
        S3 s3
) {
    public record Credentials(String accessKey, String secretKey) {
    }

    public record Region(@Name("static") String staticRegion) {
    }

    public record S3(String bucket) {
    }
}
