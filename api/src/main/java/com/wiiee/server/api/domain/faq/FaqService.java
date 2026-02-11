package com.wiiee.server.api.domain.faq;

import com.wiiee.server.api.application.faq.FaqListResponseModel;
import com.wiiee.server.api.application.faq.FaqSimpleListModel;
import com.wiiee.server.common.domain.faq.Faq;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FaqService {

    private final FaqRepository faqRepository;

    @Cacheable(value = "faq", key = "'all'")
    @Transactional(readOnly = true)
    public FaqListResponseModel getFaqAll(){

        List<Faq> faqList = faqRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        return FaqListResponseModel.builder().faqs(
                faqList.stream().map(faq -> FaqSimpleListModel.fromFaq(faq)).collect(Collectors.toList())
        ).build();
    }

}
