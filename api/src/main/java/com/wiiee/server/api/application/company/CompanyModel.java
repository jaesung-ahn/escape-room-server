package com.wiiee.server.api.application.company;

import com.wiiee.server.common.domain.common.Image;
import com.wiiee.server.common.domain.company.Company;
import com.wiiee.server.common.domain.company.CompanyBasicInfo;
import com.wiiee.server.common.domain.company.CompanyBusinessInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PROTECTED;

@Builder(access = PROTECTED)
@Value
public class CompanyModel {

    @Schema(description = "회사 아이디")
    Long companyId;
    @Schema(description = "회사명")
    String name;
    @Schema(description = "이미지")
    List<String> imageUrls;
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

    public static CompanyModel fromCompanyAndImages(Company company, List<Image> images) {
        CompanyBasicInfo basicInfo = company.getBasicInfo();
        CompanyBusinessInfo businessInfo = company.getBusinessInfo();

        String address = basicInfo.getState().getName().concat(" ")
                .concat(basicInfo.getCity().getName()).concat(" ")
                .concat(basicInfo.getAddress());

        return CompanyModel.builder()
                .companyId(company.getId())
                .name(basicInfo.getName())
//                .images(images.stream().map(ImageModel::fromImage).collect(Collectors.toList()))
                .imageUrls(images.stream().map(Image::getUrl).collect(Collectors.toList()))
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
