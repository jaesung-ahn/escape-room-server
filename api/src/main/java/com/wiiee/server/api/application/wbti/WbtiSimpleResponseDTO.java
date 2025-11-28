package com.wiiee.server.api.application.wbti;

import com.wiiee.server.common.domain.wbti.Wbti;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class WbtiSimpleResponseDTO {

    @Schema(description = "잼핏 테스트 아이디")
    Long id;

    @Schema(description = "이름")
    String name;

    Long imgId;

    @Schema(description = "이미지 url")
    String imgUrl;

    public static WbtiSimpleResponseDTO fromWbtiSimpleResponseDTO(Wbti wbti) {
        return WbtiSimpleResponseDTO.builder()
                .id(wbti.getId())
                .name(wbti.getName())
                .imgId(wbti.getWbtiImageId())
                .imgUrl(null)
                .build();
    }

    @Builder
    public WbtiSimpleResponseDTO(Long id, String name, Long imgId, String imgUrl) {
        this.id = id;
        this.name = name;
        this.imgId = imgId;
        this.imgUrl = imgUrl;
    }


}
