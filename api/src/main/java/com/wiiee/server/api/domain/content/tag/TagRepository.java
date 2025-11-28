package com.wiiee.server.api.domain.content.tag;

import com.wiiee.server.common.domain.content.tag.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findFirstByValue(String value);

}
