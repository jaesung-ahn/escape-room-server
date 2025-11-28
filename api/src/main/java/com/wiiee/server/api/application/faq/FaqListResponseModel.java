package com.wiiee.server.api.application.faq;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FaqListResponseModel {

    @Schema(description = "faq 리스트")
    List<FaqSimpleListModel> faqs;

    @Builder
    public FaqListResponseModel(List<FaqSimpleListModel> faqs) {
        this.faqs = faqs;
    }
}
