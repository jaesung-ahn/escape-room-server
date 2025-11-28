package com.wiiee.server.push.repository;

import com.wiiee.server.common.domain.push.PushHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PushHistoryRepository extends JpaRepository<PushHistory, Long> {

}
