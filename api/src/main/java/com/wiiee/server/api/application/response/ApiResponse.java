package com.wiiee.server.api.application.response;

import com.wiiee.server.api.domain.code.StatusCode;
import com.wiiee.server.api.domain.util.LocalDateTimeUtil;
import lombok.Getter;

import java.util.List;

@Getter
public class ApiResponse<T> {

    private int code;
    private String message;
    private List<String> errorDetails;
    private T data;
    private String responseTime;

    protected ApiResponse() {
    }

    private ApiResponse(int code, String message, List<String> errorDetails, T data) {
        this.code = code;
        this.message = message;
        this.errorDetails = errorDetails;
        this.data = data;
        this.responseTime = LocalDateTimeUtil.getLocalDateTimeNowString("yyyy-MM-dd hh:mm:ss");
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(StatusCode.OK_CODE, null, null, data);
    }

    public static ApiResponse<Void> successWithNoData() {
        return new ApiResponse<>(StatusCode.OK_CODE, null, null, null);
    }

    // 예외 발생으로 API 에러시 반환
    public static ApiResponse<?> error(int code, String message, List<String> errorDetails) {
        return new ApiResponse<>(code, message, errorDetails, null);
    }
}
