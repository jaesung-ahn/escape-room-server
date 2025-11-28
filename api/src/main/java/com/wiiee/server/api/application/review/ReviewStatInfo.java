package com.wiiee.server.api.application.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ReviewStatInfo {

    Double ratingAvg;
    Long reviewCnt;

}
