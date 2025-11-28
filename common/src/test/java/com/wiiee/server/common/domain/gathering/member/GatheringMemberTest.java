package com.wiiee.server.common.domain.gathering.member;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GatheringMemberTest {

    @Test
    void members_is_owner_order() {
        // given
        List<GatheringMember> members = new ArrayList<>();
        members.add(new GatheringMember(null, null, false));
        members.add(new GatheringMember(null, null, true));
        members.add(new GatheringMember(null, null, false));

        // when
        members.sort(Comparator.comparing(GatheringMember::getIsLeader).reversed());

        // then
        assertThat(members.get(0).getIsLeader()).isTrue();
    }
}