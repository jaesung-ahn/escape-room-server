package com.wiiee.server.api.application.image;

import com.wiiee.server.common.domain.common.Image;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import static lombok.AccessLevel.PROTECTED;

@Builder(access = PROTECTED)
@Value
public class ImageModel {

    @Schema(description = "이미지 아이디")
    Long id;
    @Schema(description = "이미지 URL")
    String url;

    public static ImageModel fromImage(Image image) {
        return ImageModel.builder()
                .id(image.getId())
                .url(image.getUrl())
                .build();
    }

}
