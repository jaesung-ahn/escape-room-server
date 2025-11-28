package com.wiiee.server.api.application.content.tag;

import com.wiiee.server.common.domain.content.tag.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Tag api")

@RequiredArgsConstructor
@RequestMapping("/api/tag")
@RestController
public class TagRestController {

    @Operation(summary = "태그 리스트 조회")
    @PostMapping
    public ResponseEntity<Set<Tag>> getTags() {
        return null;
    }

}
