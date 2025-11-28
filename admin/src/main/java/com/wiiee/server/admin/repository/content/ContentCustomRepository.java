package com.wiiee.server.admin.repository.content;

import com.wiiee.server.admin.form.content.ContentForm;
import com.wiiee.server.common.domain.content.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContentCustomRepository {
    Page<Content> findAllByContentGetListForm(Pageable pageable, ContentForm contentForm);
}
