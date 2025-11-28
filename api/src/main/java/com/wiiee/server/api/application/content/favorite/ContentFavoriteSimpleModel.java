package com.wiiee.server.api.application.content.favorite;

import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.common.State;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class ContentFavoriteSimpleModel {

    /**
     * Content
     */
    @Schema(description = "컨텐츠 아이디")
    private Long id;
    @Schema(description = "컨텐츠 이미지 url")
    private String imageUrl;
    @Schema(description = "컨텐츠 이름")
    private String name;
    @Schema(description = "플레이 타임")
    private String playTime;

    /**
     * Company
     */
    @Schema(description = "시도(큰 지역 단위)")
    private String state;
    @Schema(description = "시군구(작은 지역 단위)")
    private String city;
    @Schema(description = "평점")
    private Double ratingAvg;
    @Schema(description = "회사명")
    private String companyName;

    public ContentFavoriteSimpleModel(BigInteger id, String imageUrl, String name, Integer playTime, String state, String city, Double ratingAvg, String companyName) {
        this.id = id.longValue();
        this.imageUrl = imageUrl;
        this.name = name;
        this.playTime = String.valueOf(playTime).concat("분");
        this.state = State.valueOf(state).getName();
        this.city = City.valueOf(city).getName();
        this.ratingAvg = ratingAvg;
        this.companyName = companyName;
    }
}
