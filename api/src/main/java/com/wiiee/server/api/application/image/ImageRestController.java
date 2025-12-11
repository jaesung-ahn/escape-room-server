package com.wiiee.server.api.application.image;

import com.wiiee.server.api.application.response.ApiResponse;
import com.wiiee.server.api.domain.image.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Tag(name = "Image api")

@RequiredArgsConstructor
@RequestMapping("/api/image")
@RestController
public class ImageRestController {

    private final ImageService imageService;

    @Operation(summary = "이미지 등록 (파일 업로드)")
    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
    public ApiResponse<ImageModel> postImage(@Validated @ModelAttribute ImagePostRequestDTO.File dto) {
        final var imageAdded = imageService.createNewImage("image", dto.getImageFile());
        return ApiResponse.success(ImageModel.fromImage(imageAdded));
    }

    @Operation(summary = "이미지 등록 (URL)")
    @PostMapping(value = "/url", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ApiResponse<ImageModel> postImageURL(@Validated @ModelAttribute ImagePostRequestDTO.URL dto) {
        final var imageAdded = imageService.createNewImage(dto.getUrl());
        return ApiResponse.success(ImageModel.fromImage(imageAdded));
    }

    @Operation(summary = "단일 이미지 조회")
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<ImageModel> getImage(@PathVariable("id") Long id) {
        final var imageAdded = imageService.getImageById(id);
        return ApiResponse.success(ImageModel.fromImage(imageAdded));
    }

}
