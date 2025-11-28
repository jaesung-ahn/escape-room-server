package com.wiiee.server.api.application.content.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewPostRequestDTO {

    @Schema(required = true, description = "리뷰 내용")
    String message;
    @Schema(required = true, description = "별점")
    Double rating;
    @Schema(required = true, description = "참여자 수")
    Integer joinNumber;
    List<Long> imageIds;

    @Schema(required = true, description = "실제 동행 일자")
    LocalDate realGatherDate;
}
