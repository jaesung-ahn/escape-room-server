package com.wiiee.server.api.application.wbti;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class WbtiListResponseDTO {

    @Schema(description = "잼핏 테스트 리스트")
    List<WbtiSimpleResponseDTO> zamfitTest;

    public WbtiListResponseDTO(List<WbtiSimpleResponseDTO> zamfitTest) {
        this.zamfitTest = zamfitTest;
    }
}
