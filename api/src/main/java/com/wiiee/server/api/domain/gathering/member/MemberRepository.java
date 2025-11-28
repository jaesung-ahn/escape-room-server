package com.wiiee.server.api.domain.gathering.member;

import com.wiiee.server.common.domain.gathering.member.GatheringMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<GatheringMember, Long> {
}
