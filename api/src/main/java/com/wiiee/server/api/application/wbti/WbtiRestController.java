package com.wiiee.server.api.application.wbti;

import com.wiiee.server.api.application.response.ApiResponse;
import com.wiiee.server.api.application.security.AuthUser;
import com.wiiee.server.api.domain.wbti.WbtiService;
import com.wiiee.server.common.domain.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "zamfit test api")

@RequiredArgsConstructor
@RequestMapping("/api/wbti")
@RestController
public class WbtiRestController {

    private final WbtiService wbtiService;

    @Operation(summary = "잼핏테스트 목록", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ApiResponse<WbtiListResponseDTO> getWbti(@Parameter(hidden = true) @AuthUser User user) {
        return ApiResponse.success(new WbtiListResponseDTO(wbtiService.findAll()));
    }

    @Operation(summary = "잼핏테스트 저장", security = {@SecurityRequirement(name = "Authorization")})
    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public ApiResponse<?> saveWbti(@Validated @RequestBody WbtiSaveRequestDTO requestDTO,
            @Parameter(hidden = true) @AuthUser User user) {
        wbtiService.saveWbti(user.getId(), requestDTO.getWbtiId());
        return ApiResponse.successWithNoData();
    }

}
