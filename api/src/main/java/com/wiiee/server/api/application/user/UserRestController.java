package com.wiiee.server.api.application.user;

import com.wiiee.server.api.application.content.ContentSimpleModel;
import com.wiiee.server.api.application.recommendation.RecommendationModel;
import com.wiiee.server.api.application.response.ApiResponse;
import com.wiiee.server.api.application.security.AuthUser;
import com.wiiee.server.api.domain.content.ContentService;
import com.wiiee.server.api.domain.recommendation.RecommendationService;
import com.wiiee.server.api.domain.recommendation.WbtiRecommendationService;
import com.wiiee.server.api.domain.user.UserService;
import com.wiiee.server.common.domain.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "User api")

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserRestController {
    private final UserService userService;
    private final RecommendationService recommendationService;
    private final WbtiRecommendationService wbtiRecommendationService;
    private final ContentService contentService;

    @Operation(summary = "카카오 로그인/회원가입")
    @PostMapping(value = "/login/kakao", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<UserWithTokenModel> kakaoLogin(@Validated @RequestBody UserSnsRequestDTO requestDTO) {
        return ApiResponse.success(userService.kakaoLogin(requestDTO));
    }

    @Operation(summary = "유저 회원가입 나머지 정보 업데이트", security = {@SecurityRequirement(name = "Authorization")})
    @PutMapping(value = "/signup-additional", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<UserSignupEtcResponseDTO> updateUserSignupEtc(@Parameter(hidden = true) @AuthUser User authUser,
                                                                      @Validated @RequestBody UserSignupEtcRequestDTO dto) {
        return ApiResponse.success(userService.updateUserSignupEtc(authUser.getId(), dto.toUpdateUserSignupEtc()));
    }

    @Operation(summary = "유저 회원정보 수정(설정화면)", security = {@SecurityRequirement(name = "Authorization")})
    @PutMapping(value = "/settings", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<?> updateSettingUserInfo(@Parameter(hidden = true) @AuthUser User user,
                                         @Validated @RequestBody updateSettingUserInfoRequestDTO dto) {
        userService.updateSettingUserInfo(user.getId(), dto);
        return ApiResponse.successWithNoData();
    }

    @Operation(summary = "마이페이지", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/my-page", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<UserMypageResponseDTO> getMyPage(@Parameter(hidden = true) @AuthUser User authUser) {
        return ApiResponse.success(userService.getMyPage(authUser.getId()));
    }

    @Operation(summary = "유저 확인")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<UserModel> getUser(@PathVariable("id") Long id) {
        return ApiResponse.success(UserModel.from(userService.findById(id)));
    }

    @Operation(summary = "유저 수정", security = {@SecurityRequirement(name = "Authorization")})
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Void> putUser(@Parameter(hidden = true) @AuthUser User authUser,
                                          @Valid @RequestBody UserPutRequestDTO dto,
                                          @PathVariable("id") Long id) {
        userService.updateUser(authUser.getId(), id, dto.toUpdateRequest());
        return ApiResponse.successWithNoData();
    }

    @Operation(summary = "닉네임 중복 체크")
    @GetMapping(value = "/check-nickname")
    public ApiResponse<Boolean> checkNickname(@RequestParam String nickname) {
        return ApiResponse.success(userService.checkNickname(nickname));
    }

    @Operation(summary = "유저 푸시정보 변경", security = {@SecurityRequirement(name = "Authorization")})
    @PutMapping(value = "/push-info", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<?> updateUserPushInfo(@Parameter(hidden = true) @AuthUser User user,
                                                   @Validated @RequestBody UserPushInfoRequestDTO dto) {
        userService.updateUserPushInfo(user.getId(), dto);
        return ApiResponse.successWithNoData();
    }

    @Operation(summary = "내가 좋아할 만한 추천 조회", security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping(value = "/my-recommends", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<UserMyRecommendsResponseDTO> getMyRecommends(@Parameter(hidden = true) @AuthUser User authUser) {
        User user = userService.findById(authUser.getId());
        List<ContentSimpleModel> contentSimpleModelList = new ArrayList<>();
        if (user.getProfile().getWbti() != null) {
            contentSimpleModelList = contentService.getContentSimpleModelsByContents(wbtiRecommendationService.getWbtiRecommendationContents(
                    user.getProfile().getWbti().getId())
            );
        }
        List<RecommendationModel> userRecommendationList = userService.getMyWbtiRecommends(user, contentSimpleModelList);
        List<RecommendationModel> recommendationList = recommendationService.getRecommendations();

        Boolean isWbti = user.getProfile().getWbti() != null;

        List<RecommendationModel> mergedList = new ArrayList<>();
        mergedList.addAll(userRecommendationList);
        mergedList.addAll(recommendationList);

        return ApiResponse.success(UserMyRecommendsResponseDTO.fromUserMyRecommendsResponseDTO(isWbti, mergedList));
    }

    @Operation(summary = "일반 회원가입")
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<UserWithTokenModel> postUser(@RequestBody UserPostRequestDTO dto) {
        return ApiResponse.success(userService.create(dto));
    }

    @Operation(summary = "일반 로그인")
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<UserWithTokenModel> loginUser(@RequestBody UserLoginRequestDTO dto) {
        return ApiResponse.success(userService.login(dto));
    }

    @Operation(summary = "유저 푸시 알림설정 변경", security = {@SecurityRequirement(name = "Authorization")})
    @PutMapping(value = "/push-notification", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<?> updateUserPushNoti(@Parameter(hidden = true) @AuthUser User user,
                                             @Validated @RequestBody UserPushNotiRequestDTO dto) {
        userService.updateUserPushNoti(user.getId(), dto);
        return ApiResponse.successWithNoData();
    }

    @Operation(summary = "유저 로그아웃", security = {@SecurityRequirement(name = "Authorization")})
    @PostMapping(value = "/logout", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<?> logout(@Parameter(hidden = true) @AuthUser User user) {
        userService.logout(user.getId());
        return ApiResponse.successWithNoData();
    }
}
