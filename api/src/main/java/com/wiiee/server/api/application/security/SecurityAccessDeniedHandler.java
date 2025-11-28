package com.wiiee.server.api.application.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiiee.server.api.application.exception.ErrorResponse;
import com.wiiee.server.api.domain.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
@Component
public class SecurityAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public SecurityAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        log.error("SecurityAccessDenied : {} | {}", request.getRequestURI(), e.getMessage());
        ResponseUtil.writeJson(response, objectMapper, new ErrorResponse(FORBIDDEN.value(), "해당 리소스에 접근하실 수 없습니다.", Arrays.asList(e.getMessage())));
    }
}
