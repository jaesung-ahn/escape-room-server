package com.wiiee.server.api.application.content;

import com.wiiee.server.api.domain.util.LocalDateTimeUtil;
import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.common.Image;
import com.wiiee.server.common.domain.common.State;
import com.wiiee.server.common.domain.company.CompanyBasicInfo;
import com.wiiee.server.common.domain.content.Content;
import com.wiiee.server.common.domain.content.review.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Optional;

import static lombok.AccessLevel.PROTECTED;

@Setter
@Getter
public class ContentSimpleModel {

    @Schema(description = "놀거리 아이디")
    Long id;
    @Schema(description = "썸네일")
    String imageUrl;
    @Schema(description = "평점")
    Double ratingAvg;
    @Schema(description = "회사명")
    String companyName;
    @Schema(description = "시도(큰 지역 단위)")
    String state;
    @Schema(description = "시군구(작은 지역 단위)")
    String city;
    @Schema(description = "컨텐츠명")
    String contentName;
    @Schema(description = "새 컨텐츠 여부")
    Boolean isNew;
    @Schema(description = "공포 여부")
    Boolean isCaution;
    @Schema(description = "플레이 시간")
    Integer playTime;

    public static ContentSimpleModel fromContentAndImage(Content content, Image image) {
        // 평점 처리 repository 단에서 처리하는게 좋지만 list 조회에서 avg 구하는게 복잡해서 모델단에서 처리
        Double ratingAvg = content.getReviews()
                .stream()
                .filter(review -> {
                    if (review.isApproval() == true) {return true;}
                    return false;
                } )
                .mapToDouble(Review::getRating)
                .average()
                .orElse(-1);

        if (ratingAvg == -1) {
            ratingAvg = null;
        }
        CompanyBasicInfo companyBasicInfo = content.getCompany().getBasicInfo();
        return ContentSimpleModel.builder()
                .id(content.getId())
                .imageUrl(Optional.ofNullable(image).map(Image::getUrl).orElse(null))
                .ratingAvg(ratingAvg)
                .companyName(companyBasicInfo.getName())
                .state(companyBasicInfo.getState().getName())
                .city(companyBasicInfo.getCity().getName())
                .contentName(content.getContentBasicInfo().getName())
                .isNew(content.getContentBasicInfo().getIsNew())
                .isCaution(content.getContentBasicInfo().getIsCaution())
                .playTime(content.getContentBasicInfo().getPlayTime())
                .build();
    }

    private static boolean isNewCheck(LocalDateTime createAt) {
        return createAt.isAfter(LocalDateTimeUtil.getLocalDateTimeNow().minusMonths(3));
    }

    @Builder(access = PROTECTED)
    public ContentSimpleModel(Long id, String imageUrl, Double ratingAvg, String companyName, String state, String city, String contentName, Boolean isNew, Boolean isCaution, Integer playTime) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.ratingAvg = ratingAvg;
        this.companyName = companyName;
        this.state = state;
        this.city = city;
        this.contentName = contentName;
        this.isNew = isNew;
        this.isCaution = isCaution;
        this.playTime = playTime;
    }

    public ContentSimpleModel(BigInteger id, String imageUrl, Double ratingAvg, String companyName, String state, String city, String contentName, Boolean isNew, Boolean isCaution, Integer playTime) {
        this.id = id.longValue();
        this.imageUrl = imageUrl;
        this.ratingAvg = ratingAvg;
        this.companyName = companyName;
        this.state = State.valueOf(state).getName();
        this.city = City.valueOf(city).getName();
        this.contentName = contentName;
        this.isNew = isNew;
        this.isCaution = isCaution;
        this.playTime = playTime;
    }
}
