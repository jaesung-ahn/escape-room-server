package com.wiiee.server.api.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "slack")
public record SlackProperties(
        boolean enabled,
        String token,
        Channel channel
) {
    public record Channel(String monitor) {
    }
}
