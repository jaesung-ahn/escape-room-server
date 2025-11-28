package com.wiiee.server.api.application.content;

import com.wiiee.server.api.application.company.CompanySimpleModel;
import com.wiiee.server.api.application.content.price.PriceModel;
import com.wiiee.server.api.application.review.ReviewStatInfo;
import com.wiiee.server.common.domain.common.Image;
import com.wiiee.server.common.domain.company.Company;
import com.wiiee.server.common.domain.content.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PROTECTED;

@Builder(access = PROTECTED)
@Value
public class ContentModel {

    @Schema(description = "아이디")
    Long id;
    @Schema(description = "이미지")
    List<String> imageUrls;
    @Schema(description = "공포 경고 여부")
    Boolean isCaution;
    @Schema(description = "시도(큰 지역 단위)")
    String state;
    @Schema(description = "시도 코드")
    Integer stateCode;
    @Schema(description = "시군구(작은 지역 단위)")
    String city;
    @Schema(description = "시군구 코드")
    Integer cityCode;
    @Schema(description = "회사 정보")
    CompanySimpleModel company;
    @Schema(description = "상품명")
    String contentName;
    @Schema(description = "점수")
    Double ratingAvg;
    @Schema(description = "리뷰 개수")
    Long reviewCount;
    @Schema(description = "가격")
    List<PriceModel> prices;
    @Schema(description = "장르")
    String genre;
    @Schema(description = "장르 코드")
    Integer genreCode;
    @Schema(description = "최소 인원")
    Integer minPeople;
    @Schema(description = "최대 인원")
    Integer maxPeople;
    @Schema(description = "난이도")
    String difficulty;
    @Schema(description = "활동성")
    String activityLevel;
    @Schema(description = "유형")
    String escapeType;
    @Schema(description = "시간")
    Integer playTime;
    @Schema(description = "상품 설명")
    String information;

    public static ContentModel fromContentAndImagesWithCompanySimpleModel(Content content, List<Image> images,
                                                                          CompanySimpleModel companySimpleModel,
                                                                          ReviewStatInfo reviewInfo) {
        final var basicInfo = content.getContentBasicInfo();

        Company company = content.getCompany();

        return ContentModel.builder()
                .id(content.getId())
                .imageUrls(images.stream().map(Image::getUrl).collect(Collectors.toList()))
                .state(companySimpleModel.getState())
                .stateCode(company.getBasicInfo().getState().getCode())
                .city(companySimpleModel.getCity())
                .cityCode(company.getBasicInfo().getCity().getCode())
                .isCaution(basicInfo.getIsCaution())
                .contentName(basicInfo.getName())
                .ratingAvg(reviewInfo.getRatingAvg())
                .reviewCount(reviewInfo.getReviewCnt())
                .prices(content.getContentPrices().stream().map(PriceModel::fromPrice).collect(Collectors.toList()))
                .genre(basicInfo.getGenre().getName())
                .genreCode(basicInfo.getGenre().getCode())
                .minPeople(basicInfo.getMinPeople())
                .maxPeople(basicInfo.getMaxPeople())
                .difficulty(basicInfo.getDifficulty().getName())
                .activityLevel(basicInfo.getActivityLevel().getName())
                .escapeType(basicInfo.getEscapeType().getName())
                .playTime(basicInfo.getPlayTime())
                .information(basicInfo.getInformation())
                .company(companySimpleModel)
                .build();
    }

    private static boolean isNewCheck(LocalDateTime createAt) {
        return true;
    }

}
