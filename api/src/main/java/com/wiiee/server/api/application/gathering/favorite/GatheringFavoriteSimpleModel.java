package com.wiiee.server.api.application.gathering.favorite;

import com.wiiee.server.common.domain.gathering.GatheringStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigInteger;

@Getter
public class GatheringFavoriteSimpleModel {

    /**
     * Gathering
     */
    @Schema(description = "동행모집 아이디")
    private Long id;
    @Schema(description = "동행 모집 타이틀")
    private String title;
    @Schema(description = "최대 인원")
    private Integer maxPeople;
    @Schema(description = "현재 인원")
    private Integer currentPeople;

    @Schema(description = "모임 상태 명칭")
    private String gatheringStatusName;

    /**
     * Content
     */
    @Schema(description = "컨텐츠 이미지 url")
    private String imageUrl;
    @Schema(description = "컨텐츠명")
    private String contentName;
    @Schema(description = "시도(큰 지역 단위)")
    private String state;
    @Schema(description = "시군구(작은 지역 단위)")
    private String city;
    @Schema(description = "평점")
    private Double ratingAvg;
    @Schema(description = "회사명")
    private String companyName;

    public GatheringFavoriteSimpleModel(BigInteger id, String title, Integer maxPeople, String gatheringStatusName, BigInteger currentPeople, String imageUrl, String contentName, String state, String city, Double ratingAvg, String companyName) {
        this.id = id.longValue();
        this.title = title;
        this.maxPeople = maxPeople;
        this.gatheringStatusName = GatheringStatus.valueOf(gatheringStatusName).getName();
        this.currentPeople = currentPeople.intValue();
        this.imageUrl = imageUrl;
        this.contentName = contentName;
        this.state = state;
        this.city = city;
        this.ratingAvg = ratingAvg;
        this.companyName = companyName;
    }
}
