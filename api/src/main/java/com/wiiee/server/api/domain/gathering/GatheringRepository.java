package com.wiiee.server.api.domain.gathering;

import com.wiiee.server.api.application.gathering.GatheringGetRequestDTO;
import com.wiiee.server.api.infrastructure.repository.gathering.GatheringCustomRepository;
import com.wiiee.server.common.domain.gathering.Gathering;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GatheringRepository extends JpaRepository<Gathering, Long>, GatheringCustomRepository {
    Page<Gathering> findAllByGatheringGetRequestDTO(GatheringGetRequestDTO dto, Pageable pageable);

}
