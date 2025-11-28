package com.wiiee.server.api.domain.gathering;

import com.wiiee.server.common.domain.gathering.Gathering;
import com.wiiee.server.common.domain.gathering.member.GatheringMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GatheringMemberRepository extends JpaRepository<GatheringMember, Long> {

    List<GatheringMember> findAllByGathering(Gathering gatheringId);
}
