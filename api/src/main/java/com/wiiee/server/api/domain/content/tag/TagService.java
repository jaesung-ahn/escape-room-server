package com.wiiee.server.api.domain.content.tag;

import com.wiiee.server.common.domain.content.tag.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TagService {

    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public Set<Tag> reloadAllTagsIfAlreadyPresent(Set<Tag> tags) {
        return tags.stream()
                .map(tag -> findByValue(tag.getValue()).orElse(tag))
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public Optional<Tag> findByValue(String value) {
        return tagRepository.findFirstByValue(value);
    }
}
