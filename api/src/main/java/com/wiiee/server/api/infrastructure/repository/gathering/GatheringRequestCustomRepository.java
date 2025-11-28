package com.wiiee.server.api.infrastructure.repository.gathering;

import com.wiiee.server.common.domain.gathering.Gathering;
import com.wiiee.server.common.domain.gathering.request.GatheringRequest;
import com.wiiee.server.common.domain.user.User;

import java.util.List;

public interface GatheringRequestCustomRepository {

    List<GatheringRequest> findAllByGathering(Gathering Gathering);
    long countUnverifiedRequestByGathering(Gathering Gathering);

    GatheringRequest findApprovedGatheringRequest(Gathering gathering, User user);
}
