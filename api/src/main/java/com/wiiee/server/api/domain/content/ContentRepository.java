package com.wiiee.server.api.domain.content;

import com.wiiee.server.api.application.content.ContentGetRequestDTO;
import com.wiiee.server.api.infrastructure.repository.content.ContentCustomRepository;
import com.wiiee.server.common.domain.content.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long>, ContentCustomRepository {

    @Override
    Page<Content> findAllByContentGetRequestDTO(ContentGetRequestDTO dto, Pageable pageable);

}