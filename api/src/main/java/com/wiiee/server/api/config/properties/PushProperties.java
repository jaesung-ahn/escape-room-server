package com.wiiee.server.api.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "push")
public record PushProperties(
        boolean enabled,
        Api api
) {
    public record Api(String url) {
    }
}
