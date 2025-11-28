package com.wiiee.server.api.application.page;

import com.wiiee.server.api.application.content.ContentSimpleModel;
import com.wiiee.server.api.application.content.review.ReviewSimpleModel;
import com.wiiee.server.api.application.event.EventModel;
import com.wiiee.server.api.application.keyword.KeywordModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import java.util.List;

@Value
public class MainPageModel {

    @Schema(description = "상단 메인이벤트")
    List<EventModel> mainEvents;
    @Schema(description = "내가 좋아할 만한 추천")
    List<ContentSimpleModel> recommendContents;
    @Schema(description = "상단 이벤트")
    List<EventModel> topEvents;
    @Schema(description = "내 지역 방탈출 모아보기")
    List<ContentSimpleModel> myPlaceContents;
    @Schema(description = "요즘 핫한 방탈출 차트")
    List<ContentSimpleModel> hotContents;
//    @Schema(description = "인기 키워드 바로가기")
//    List<KeywordModel> popularityKeyword;
    @Schema(description = "실시간 리뷰")
    List<ReviewSimpleModel> realTimeReviews;
    @Schema(description = "하단 이벤트")
    List<EventModel> bottomEvents;

    public static MainPageModel of(List<EventModel> mainEvents, List<ContentSimpleModel> recommendContents, List<EventModel> topEvents, List<ContentSimpleModel> myPlaceContents, List<ContentSimpleModel> hotContents/*, List<KeywordModel> popularityKeyword*/, List<ReviewSimpleModel> realTimeReviews, List<EventModel> bottomEvents) {
        return new MainPageModel(
                mainEvents,
                recommendContents,
                topEvents,
                myPlaceContents,
                hotContents,
//                popularityKeyword,
                realTimeReviews,
                bottomEvents
        );
    }

}