package com.wiiee.server.push.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public GroupedOpenApi pageApi() {
        return GroupedOpenApi.builder()
                .group("push-api")
                .pathsToMatch("/api/push/**")
                .build();
    }

}
