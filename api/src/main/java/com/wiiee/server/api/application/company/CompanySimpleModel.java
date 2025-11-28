package com.wiiee.server.api.application.company;

import com.wiiee.server.common.domain.common.Image;
import com.wiiee.server.common.domain.company.Company;
import com.wiiee.server.common.domain.company.CompanyBasicInfo;
import com.wiiee.server.common.domain.company.CompanyBusinessInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PROTECTED;

@Builder(access = PROTECTED)
@Value
public class CompanySimpleModel {

    @Schema(description = "회사 아이디")
    Long companyId;
    @Schema(description = "회사명")
    String name;
    @Schema(description = "썸네일")
    String imageUrl;
    @Schema(description = "시도(큰 지역 단위)")
    String state;
    @Schema(description = "시군구(작은 지역 단위)")
    String city;
    @Schema(description = "상세 주소")
    String address;
    @Schema(description = "일(1), 월(2), 화(3), 수(4), 목(5), 금(6), 토(7)")
    List<Integer> businessDay;
    @Schema(description = "연중무휴")
    Boolean isAlwaysOperated;
    @Schema(description = "연락처")
    String contact;
    @Schema(description = "사이트 URL")
    String url;
    @Schema(description = "공지사항")
    String notice;

    public static CompanySimpleModel fromCompanyAndImage(Company company, Image image) {
        CompanyBasicInfo basicInfo = company.getBasicInfo();
        CompanyBusinessInfo businessInfo = company.getBusinessInfo();

        return CompanySimpleModel.builder()
                .companyId(company.getId())
                .name(basicInfo.getName())
                .imageUrl(Optional.ofNullable(image).map(Image::getUrl).orElse(null))
                .state(basicInfo.getState().getName())
                .city(basicInfo.getCity().getName())
                .address(basicInfo.getAddress())
                .businessDay(basicInfo.getBusinessDayCodes())
                .isAlwaysOperated(basicInfo.getIsOperated())
                .contact(basicInfo.getContact())
                .url(basicInfo.getUrl())
                .notice(basicInfo.getNotice())
                .build();
    }
}
