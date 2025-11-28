package com.wiiee.server.api.application.event;

import com.wiiee.server.api.application.response.ApiResponse;
import com.wiiee.server.api.application.security.AuthUser;
import com.wiiee.server.api.domain.event.EventService;
import com.wiiee.server.common.domain.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Event api")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/event")
public class EventRestController {

    private final EventService eventService;

    @Operation(summary = "이벤트 검색", security = {@SecurityRequirement(name = "Authorization")}, deprecated = true)
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ApiResponse<MultipleEventModel> getEvents(@ModelAttribute @Valid EventGetRequestDTO dto,
                                                        @Parameter(hidden = true) @AuthUser User user) {
        return ApiResponse.success(eventService.getEvents(dto));
    }

    @Operation(summary = "이벤트 리스트 조회(설정화면)", security = {@SecurityRequirement(name = "Authorization")})
    @PostMapping(value = "/setting-list", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<EventSettingListResponseModel> getSettingList(@Parameter(hidden = true) @AuthUser User user) {
        return ApiResponse.success(eventService.getEventSettingList());
    }

    @Operation(summary = "이벤트 상세 조회", security = {@SecurityRequirement(name = "Authorization")})
    @PostMapping(value = "/detail", produces = APPLICATION_JSON_VALUE)
    public ApiResponse<EventDetailModel> getEventDetail(@Validated @RequestBody EventDetailRequestDTO dto,
                                                     @Parameter(hidden = true) @AuthUser User user) {
        return ApiResponse.success(eventService.getEventDetail(dto.getEventId()));
    }

}