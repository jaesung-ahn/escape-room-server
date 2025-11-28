package com.wiiee.server.api.application.wbti;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.NotNull;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class WbtiSaveRequestDTO {

    @Schema(description = "잼핏 테스트 id")
    @NotNull(message = "잼핏 테스트 id를 입력해주세요.")
    Long wbtiId;

    @Builder
    public WbtiSaveRequestDTO(Long wbtiId) {
        this.wbtiId = wbtiId;
    }
}
