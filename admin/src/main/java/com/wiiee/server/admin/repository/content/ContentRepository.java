package com.wiiee.server.admin.repository.content;

import com.wiiee.server.common.domain.content.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long> {
}
