package com.wiiee.server.api.domain.faq;

import com.wiiee.server.common.domain.faq.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqRepository extends JpaRepository<Faq, Long> {
}
