package com.wiiee.server.api.infrastructure.repository.content;

import lombok.Value;

import java.util.List;

@Value
public class ContentResponseDTO {

    Long contentId;
    List<String> images;
    Boolean isCaution;
    String companyName;
    Double ratingAvg;
    Integer reviewCount;
    String contentName;

    public ContentResponseDTO(Long contentId, List<String> images, Boolean isCaution, String companyName, Double ratingAvg, Integer reviewCount, String contentName) {
        this.contentId = contentId;
        this.images = images;
        this.isCaution = isCaution;
        this.companyName = companyName;
        this.ratingAvg = ratingAvg;
        this.reviewCount = reviewCount;
        this.contentName = contentName;
    }
}
