package com.wiiee.server.api.application.gathering.member;

import com.wiiee.server.api.application.response.ApiResponse;
import com.wiiee.server.api.application.security.AuthUser;
import com.wiiee.server.api.domain.gathering.member.MemberService;
import com.wiiee.server.common.domain.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Member api")

@RequiredArgsConstructor
@RequestMapping("/api/member")
@RestController
public class MemberRestController {

    private final MemberService memberService;

    @Operation(summary = "멤버 등록", security = {@SecurityRequirement(name = "Authorization")})
    @PostMapping(value = "/gathering/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ApiResponse<MemberModel> postMember(@Parameter(hidden = true) @AuthUser User user,
                                                     @PathVariable("id") Long gatheringId) {
        return ApiResponse.success(memberService.addMember(user.getId(), gatheringId));
    }

    @Operation(summary = "멤버 수정", security = {@SecurityRequirement(name = "Authorization")})
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ApiResponse<MemberModel> putMember(@Parameter(hidden = true) @AuthUser User user,
                                                 @PathVariable("id") Long id,
                                                 @RequestParam Integer statusCode) {
        return ApiResponse.success(memberService.updateMember(id, statusCode));
    }

}