package com.wiiee.server.api.application.gathering;

import lombok.Value;

import java.util.List;

@Value
public class MultipleGatheringModel {

    List<GatheringSimpleModel> gatherings;
    long count;
    Boolean hasNext;

    public static MultipleGatheringModel fromGatherings(List<GatheringSimpleModel> gatherings, long totalCount, boolean hasNext) {
        return new MultipleGatheringModel(gatherings, totalCount, hasNext);
    }

}
