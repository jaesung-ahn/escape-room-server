package com.wiiee.server.api.application.common;

import com.wiiee.server.api.application.response.ApiResponse;
import com.wiiee.server.api.domain.appVersion.AppVersionService;
import com.wiiee.server.common.domain.common.State;
import com.wiiee.server.common.domain.content.Difficulty;
import com.wiiee.server.common.domain.content.Genre;
import com.wiiee.server.common.domain.gathering.AgeGroup;
import com.wiiee.server.common.domain.gathering.GenderType;
import com.wiiee.server.common.domain.gathering.RecruitType;
import com.wiiee.server.common.domain.user.UserOS;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Util api")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/util")
@RestController
public class UtilRestController {

    private final AppVersionService appVersionService;

    @Operation(summary = "시군구 리스트 조회")
    @GetMapping(value = "/states", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<List<StateModel>> getStates() {
        return ApiResponse.success(Arrays.stream(State.values())
                .map(StateModel::new)
                .collect(Collectors.toList()));
    }

    @Operation(summary = "연령대 리스트 조회")
    @GetMapping(value = "/age-groups", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<List<AgeGroupInfoModel>> getAgeGroup() {
        return ApiResponse.success(Arrays.stream(AgeGroup.values())
                .map(AgeGroupInfoModel::new)
                .collect(Collectors.toList()));
    }

    @Operation(summary = "성별타입 리스트 조회")
    @GetMapping(value = "/gender-types", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<List<GenderTypeModel>> getGenderType() {
        return ApiResponse.success(Arrays.stream(GenderType.values())
                .map(GenderTypeModel::new)
                .collect(Collectors.toList()));
    }

    @Operation(summary = "모집타입 리스트 조회")
    @GetMapping(value = "/recruit-types", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<List<RecruitTypeModel>> getRecruitType() {
        return ApiResponse.success(Arrays.stream(RecruitType.values())
                .map(RecruitTypeModel::new)
                .collect(Collectors.toList()));
    }

    @Operation(summary = "장르 리스트 조회")
    @GetMapping(value = "/genre", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<List<GenreModel>> getGenre() {
        return ApiResponse.success(Arrays.stream(Genre.values())
                .map(GenreModel::new)
                .collect(Collectors.toList()));
    }

    @Operation(summary = "난이도 리스트 조회")
    @GetMapping(value = "/difficulty", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<List<DifficultyModel>> getDifficulty() {
        return ApiResponse.success(Arrays.stream(Difficulty.values())
                .map(DifficultyModel::new)
                .collect(Collectors.toList()));
    }

    @Operation(summary = "유저 OS 리스트 조회")
    @GetMapping(value = "/user-os", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<List<UserOSModel>> getUserOs() {
        return ApiResponse.success(Arrays.stream(UserOS.values())
                .map(UserOSModel::new)
                .collect(Collectors.toList()));
    }

    @Operation(summary = "초기 정보 조회")
    @PostMapping(value = "/init-information", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<InitInformationResponseDTO> getInitInformation(@Validated @RequestBody InitInformationRequestDTO dto) {
        log.info("call UtilRestController.getInitInformation()");
        return ApiResponse.success(InitInformationResponseDTO.builder()
                .appVersionModel(appVersionService.checkLatestVersion(dto))
                .build()) ;
    }

}