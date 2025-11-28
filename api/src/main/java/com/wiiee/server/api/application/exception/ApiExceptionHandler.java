package com.wiiee.server.api.application.exception;

import com.wiiee.server.api.application.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ApiResponse<?> handleCustomException(CustomException ce, WebRequest request) {
        log.error("CustomException occurred: code={}, message={}", ce.getCode(), ce.getMessage(), ce);
        return ApiResponse.error(ce.getCode(), ce.getMessage(), ce.getErrorDetails());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<?> handlerRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("Unexpected RuntimeException at {}: {}", request.getRequestURI(), e.getMessage(), e);
        return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "처리 중 오류가 발생했습니다.", Arrays.asList(request.getRequestURI()));
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UsernameNotFoundException.class)
    public ApiResponse<?> handlerHttpUsernameNotFoundException(UsernameNotFoundException e, HttpServletRequest request) {
        log.error("UsernameNotFoundException at {}: {}", request.getRequestURI(), e.getMessage(), e);
        return ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "사용자를 찾을 수 없습니다.", Arrays.asList("인증 실패"));
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<?> handlerIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException: {}", e.getMessage(), e);
        return ApiResponse.error(BAD_REQUEST.value(), "잘못된 요청입니다.", Arrays.asList(e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("Validation failed: {}", e.getBindingResult().getAllErrors().get(0).getDefaultMessage(), e);
        return ApiResponse.error(BAD_REQUEST.value(), e.getBindingResult().getAllErrors().get(0).getDefaultMessage(), Arrays.asList("입력값 검증 실패"));
    }

}
