package com.wiiee.server.api.domain.notice;

import com.wiiee.server.common.domain.notice.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

}
