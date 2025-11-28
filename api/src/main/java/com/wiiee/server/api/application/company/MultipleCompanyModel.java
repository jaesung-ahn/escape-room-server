package com.wiiee.server.api.application.company;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import java.util.List;

@Value
public class MultipleCompanyModel {

    @Schema(description = "회사 리스트")
    List<CompanySimpleModel> companies;

    @Schema(description = "표출 개수")
    int count;

    @Schema(description = "다음 페이지 유무")
    boolean hasNext;

    public static MultipleCompanyModel fromCompaniesAndHasNext(List<CompanySimpleModel> companies, boolean hasNext) {
        return new MultipleCompanyModel(companies, companies.size(), hasNext);
    }

}
