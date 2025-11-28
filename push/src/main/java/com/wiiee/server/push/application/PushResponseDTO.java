package com.wiiee.server.push.application;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import static lombok.AccessLevel.PROTECTED;

@Builder(access = PROTECTED)
@Value
public class PushResponseDTO {

    @Schema(description = "코드")
    String code;

    @Schema(description = "메시지")
    String msg;

    public static PushResponseDTO fromPushResponseDTO(String code, String msg) {
        return PushResponseDTO.builder()
                .code(code)
                .msg(msg)
                .build();
    }
}
