package com.wiiee.server.api.application.gathering;

import com.wiiee.server.common.domain.common.Image;
import com.wiiee.server.common.domain.company.CompanyBasicInfo;
import com.wiiee.server.common.domain.content.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Optional;

import static lombok.AccessLevel.PROTECTED;

@Builder(access = PROTECTED)
@Setter
@Getter
@ToString
public class GatheringListContentModel {

    @Schema(description = "놀거리명")
    String contentName;
    @Schema(description = "썸네일")
    String imageUrl;
    @Schema(description = "회사명")
    String companyName;
    @Schema(description = "시도(큰 지역 단위)")
    String state;
    @Schema(description = "시군구(작은 지역 단위)")
    String city;

    @Schema(description = "new 표시 여부")
    Boolean isNew;
    @Schema(description = "공포 여부")
    Boolean isCaution;

    public static GatheringListContentModel fromContentAndImage(Content content, Image image) {
        CompanyBasicInfo companyBasicInfo = content.getCompany().getBasicInfo();
        return GatheringListContentModel.builder()
                .contentName(content.getContentBasicInfo().getName())
                .imageUrl(Optional.ofNullable(image).map(Image::getUrl).orElse(null))
                .companyName(companyBasicInfo.getName())
                .state(companyBasicInfo.getState().getName())
                .city(companyBasicInfo.getCity().getName())
                .isNew(content.getContentBasicInfo().getIsNew())
                .isCaution(content.getContentBasicInfo().getIsCaution())
                .build();
    }
}
