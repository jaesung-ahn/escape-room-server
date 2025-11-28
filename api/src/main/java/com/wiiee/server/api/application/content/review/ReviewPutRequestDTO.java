package com.wiiee.server.api.application.content.review;

import lombok.Value;

import java.util.List;

@Value
public class ReviewPutRequestDTO {

    String message;
    Double rating;
    List<Long> imageIds;

}
