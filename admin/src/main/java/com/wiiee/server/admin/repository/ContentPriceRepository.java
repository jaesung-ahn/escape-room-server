package com.wiiee.server.admin.repository;

import com.wiiee.server.common.domain.content.price.ContentPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentPriceRepository extends JpaRepository<ContentPrice, Long> {

}
