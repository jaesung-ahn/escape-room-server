package com.wiiee.server.api.domain.gathering;

import com.wiiee.server.api.infrastructure.repository.gathering.GatheringRequestCustomRepository;
import com.wiiee.server.common.domain.gathering.request.GatheringRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GatheringRequestRepository extends JpaRepository<GatheringRequest, Long>, GatheringRequestCustomRepository {

}
