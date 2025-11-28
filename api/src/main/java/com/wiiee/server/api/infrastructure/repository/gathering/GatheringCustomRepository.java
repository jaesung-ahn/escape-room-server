package com.wiiee.server.api.infrastructure.repository.gathering;

import com.wiiee.server.api.application.gathering.GatheringGetRequestDTO;
import com.wiiee.server.common.domain.gathering.Gathering;
import com.wiiee.server.common.domain.gathering.member.GatheringMember;
import com.wiiee.server.common.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GatheringCustomRepository {

    Page<Gathering> findAllByGatheringGetRequestDTO(GatheringGetRequestDTO dto, Pageable pageable);

    int findCountByUser(User user);

    List<Gathering> findAllMyGathering(User user);

    GatheringMember findGatheringMember(Gathering gathering, User user);

}
