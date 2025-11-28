package com.wiiee.server.api.domain.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiiee.server.api.application.exception.ErrorResponse;
import org.springframework.http.MediaType;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseUtil {

    public static void writeJson(HttpServletResponse response, ObjectMapper objectMapper, ErrorResponse errorResponse) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
