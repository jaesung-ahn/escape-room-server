package com.wiiee.server.api.domain.wbti;

import com.wiiee.server.common.domain.wbti.Wbti;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WbtiRepository extends JpaRepository<Wbti, Long> {
}
