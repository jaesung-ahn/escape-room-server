package com.wiiee.server.admin.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ModelMapper 매번 생성하는 것 지양
 */
@Configuration
public class RootContext {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
