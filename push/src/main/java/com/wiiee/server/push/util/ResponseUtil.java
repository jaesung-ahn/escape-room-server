package com.wiiee.server.push.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiiee.server.push.response.DefaultResponse;
import org.springframework.http.MediaType;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseUtil {

    public static void writeJson(HttpServletResponse response, ObjectMapper objectMapper, DefaultResponse defaultResponse) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(defaultResponse));
    }
}
