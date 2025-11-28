package com.wiiee.server.api.infrastructure;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public GroupedOpenApi jwtApi() {
        return GroupedOpenApi.builder()
                .group("jwt-api")
                .pathsToMatch("/api/jwt/**")
                .build();
    }

    @Bean
    public GroupedOpenApi pageApi() {
        return GroupedOpenApi.builder()
                .group("page-api")
                .pathsToMatch("/api/page/**")
                .build();
    }

    @Bean
    public GroupedOpenApi imageApi() {
        return GroupedOpenApi.builder()
                .group("image-api")
                .pathsToMatch("/api/image/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("user-api")
                .pathsToMatch("/api/user/**")
                .build();
    }

    @Bean
    public GroupedOpenApi contentApi() {
        return GroupedOpenApi.builder()
                .group("content-api")
                .pathsToMatch("/api/content/**")
                .build();
    }

    @Bean
    public GroupedOpenApi companyApi() {
        return GroupedOpenApi.builder()
                .group("company-api")
                .pathsToMatch("/api/company/**")
                .build();
    }

    @Bean
    public GroupedOpenApi tagApi() {
        return GroupedOpenApi.builder()
                .group("tag-api")
                .pathsToMatch("/api/tag/**")
                .build();
    }

    @Bean
    public GroupedOpenApi reviewApi() {
        return GroupedOpenApi.builder()
                .group("review-api")
                .pathsToMatch("/api/review/**")
                .build();
    }

    @Bean
    public GroupedOpenApi gatheringApi() {
        return GroupedOpenApi.builder()
                .group("gathering-api")
                .pathsToMatch("/api/gathering/**")
                .build();
    }

    @Bean
    public GroupedOpenApi memberApi() {
        return GroupedOpenApi.builder()
                .group("member-api")
                .pathsToMatch("/api/member/**")
                .build();
    }

    @Bean
    public GroupedOpenApi eventApi() {
        return GroupedOpenApi.builder()
                .group("event-api")
                .pathsToMatch("/api/event/**")
                .build();
    }

    @Bean
    public GroupedOpenApi utilApi() {
        return GroupedOpenApi.builder()
                .group("util-api")
                .pathsToMatch("/api/util/**")
                .build();
    }

    @Bean
    public GroupedOpenApi wbtiApi() {
        return GroupedOpenApi.builder()
                .group("zamfit-api")
                .pathsToMatch("/api/wbti/**")
                .build();
    }

    @Bean
    public GroupedOpenApi commentApi() {
        return GroupedOpenApi.builder()
                .group("comment-api")
                .pathsToMatch("/api/comment/**")
                .build();
    }

    @Bean
    public GroupedOpenApi noticeApi() {
        return GroupedOpenApi.builder()
                .group("notice-api")
                .pathsToMatch("/api/notice/**")
                .build();
    }

    @Bean
    public OpenAPI wiieeApiServer() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("Authorization",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("Bearer").bearerFormat("JWT")))
                .info(new Info().title("Wiiee API Server").version("v1"));
//                .externalDocs(new ExternalDocumentation())

    }

}
