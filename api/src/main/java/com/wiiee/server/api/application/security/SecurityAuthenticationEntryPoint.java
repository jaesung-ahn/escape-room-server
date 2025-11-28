package com.wiiee.server.api.application.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiiee.server.api.application.exception.ErrorResponse;
import com.wiiee.server.api.domain.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;


@Slf4j
@Component
public class SecurityAuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public SecurityAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        log.error("UNAUTHORIZED : {} | {}", request.getRequestURI(), e.getMessage());
        ResponseUtil.writeJson(response, objectMapper, new ErrorResponse(UNAUTHORIZED.value(), "올바르지 못한 인증입니다.", Arrays.asList(e.getMessage())));
    }
}
