package com.wiiee.server.common.domain.gathering;

import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.common.State;

import java.time.LocalDate;

public class FixtureGathering extends Gathering{

    public FixtureGathering(String title) {
        super(
                null,
                null,
                new GatheringInfo(
                        title,
                        "information",
                        State.SEOUL,
                        City.SONGPAGU,
                        RecruitType.FIRST_COME,
                        4,
                        GenderType.IRRELEVANT,
                        true,
                        LocalDate.now(),
                        "",
                        GatheringStatus.RECRUITING
                )
        );
    }

}
