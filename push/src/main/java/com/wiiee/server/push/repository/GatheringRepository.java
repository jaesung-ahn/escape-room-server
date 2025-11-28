package com.wiiee.server.push.repository;

import com.wiiee.server.common.domain.gathering.Gathering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GatheringRepository extends JpaRepository<Gathering, Long> {

}
