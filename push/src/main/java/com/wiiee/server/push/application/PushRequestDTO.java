package com.wiiee.server.push.application;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class PushRequestDTO {


    @Schema(required = true, description = "푸시 받을 유저 id")
    private Long userId;
//    @Schema(required = true, description = "푸시 유형")
//    private PushType pushType;

//    @Builder
//    public PushRequestDTO(Long userId, PushType pushType) {
//        this.userId = userId;
//        this.pushType = pushType;
//    }
}
