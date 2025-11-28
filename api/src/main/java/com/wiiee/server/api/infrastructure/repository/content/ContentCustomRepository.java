package com.wiiee.server.api.infrastructure.repository.content;

import com.wiiee.server.api.application.content.ContentGetRequestDTO;
import com.wiiee.server.api.application.content.ContentResponseDTO;
import com.wiiee.server.common.domain.content.Content;
import com.wiiee.server.common.domain.content.RankContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ContentCustomRepository {

    Page<Content> findAllByContentGetRequestDTO(ContentGetRequestDTO dto, Pageable pageable);

    Page<ContentResponseDTO> findContentModelByContentGetRequestDTO(ContentGetRequestDTO dto, Pageable pageable);

    List<RankContent> findAllMainHotContent(int limit);
}