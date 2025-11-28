package com.wiiee.server.admin.repository;

import com.wiiee.server.common.domain.gathering.Gathering;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GatheringRepository extends JpaRepository<Gathering, Long> {

}
