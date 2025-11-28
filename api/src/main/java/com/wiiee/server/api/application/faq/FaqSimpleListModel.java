package com.wiiee.server.api.application.faq;

import com.wiiee.server.common.domain.faq.Faq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FaqSimpleListModel {

    @Schema(description = "faq 아이디")
    Long id;
    @Schema(description = "faq 질문")
    String question;

    @Schema(description = "faq 답변")
    String answer;

    public static FaqSimpleListModel fromFaq(Faq faq) {
        return new FaqSimpleListModel(
                faq.getId(),
                faq.getQuestion(),
                faq.getAnswer()
        );
    }
}
