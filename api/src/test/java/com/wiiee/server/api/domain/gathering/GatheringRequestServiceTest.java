package com.wiiee.server.api.domain.gathering;

import com.wiiee.server.api.application.exception.BadRequestException;
import com.wiiee.server.api.application.exception.ConflictException;
import com.wiiee.server.api.application.exception.ForbiddenException;
import com.wiiee.server.api.application.gathering.GatheringCancelReqDTO;
import com.wiiee.server.api.application.gathering.GatheringConfirmReqDTO;
import com.wiiee.server.api.domain.image.ImageService;
import com.wiiee.server.api.domain.user.UserService;
import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.common.State;
import com.wiiee.server.common.domain.content.Content;
import com.wiiee.server.common.domain.content.ContentBasicInfo;
import com.wiiee.server.common.domain.gathering.*;
import com.wiiee.server.common.domain.gathering.member.GatheringMember;
import com.wiiee.server.common.domain.gathering.request.GatheringRequest;
import com.wiiee.server.common.domain.gathering.request.GatheringRequestStatus;
import com.wiiee.server.common.domain.user.FixtureUser;
import com.wiiee.server.common.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GatheringRequestService 단위 테스트")
class GatheringRequestServiceTest {

    @Mock
    private GatheringRepository gatheringRepository;
    @Mock
    private GatheringRequestRepository gatheringRequestRepository;
    @Mock
    private UserService userService;
    @Mock
    private ImageService imageService;
    @Mock
    private GatheringNotificationService gatheringNotificationService;

    @InjectMocks
    private GatheringRequestService gatheringRequestService;

    private User user;
    private User host;
    private Content content;

    @BeforeEach
    void setUp() {
        user = new FixtureUser("user@test.com", "테스터");
        host = new FixtureUser("host@test.com", "호스트");
        ReflectionTestUtils.setField(host, "id", 2L);
        content = new Content(null, new ContentBasicInfo("테스트 놀거리"));
        ReflectionTestUtils.setField(content, "id", 1L);
    }

    private Gathering createGathering(RecruitType recruitType, GatheringStatus status, int maxPeople) {
        GatheringInfo info = new GatheringInfo("테스트 동행", "설명", State.SEOUL, City.GANGNAMGU,
                recruitType, maxPeople, GenderType.IRRELEVANT, true, LocalDate.now().plusDays(7), "", status);
        Gathering gathering = new Gathering(content, host, info);
        ReflectionTestUtils.setField(gathering, "id", 10L);
        ReflectionTestUtils.setField(gathering, "gatheringMembers", new HashSet<>(Set.of(new GatheringMember(host, gathering, true))));
        return gathering;
    }

    @Nested
    @DisplayName("applyGathering")
    class ApplyGathering {

        @Test
        @DisplayName("모집완료 상태의 동행모집에 신청하면 ConflictException 발생")
        void throwsConflictWhenRecruitCompleted() {
            // given
            Gathering gathering = createGathering(RecruitType.FIRST_COME, GatheringStatus.RECRUIT_COMPLETED, 4);
            given(userService.findById(1L)).willReturn(user);
            given(gatheringRepository.findById(10L)).willReturn(Optional.of(gathering));

            // when & then
            assertThatThrownBy(() -> gatheringRequestService.applyGathering(10L, 1L, null))
                    .isInstanceOf(ConflictException.class);
        }

        @Test
        @DisplayName("만료된 동행모집에 신청하면 ConflictException 발생")
        void throwsConflictWhenRecruitExpired() {
            // given
            Gathering gathering = createGathering(RecruitType.FIRST_COME, GatheringStatus.RECRUIT_EXPIRED, 4);
            given(userService.findById(1L)).willReturn(user);
            given(gatheringRepository.findById(10L)).willReturn(Optional.of(gathering));

            // when & then
            assertThatThrownBy(() -> gatheringRequestService.applyGathering(10L, 1L, null))
                    .isInstanceOf(ConflictException.class);
        }

        @Test
        @DisplayName("최대 인원이 가득 찬 동행모집에 신청하면 ConflictException 발생")
        void throwsConflictWhenMaxMember() {
            // given
            Gathering gathering = createGathering(RecruitType.FIRST_COME, GatheringStatus.RECRUITING, 1);
            given(userService.findById(1L)).willReturn(user);
            given(gatheringRepository.findById(10L)).willReturn(Optional.of(gathering));

            // when & then
            assertThatThrownBy(() -> gatheringRequestService.applyGathering(10L, 1L, null))
                    .isInstanceOf(ConflictException.class);
        }

        @Test
        @DisplayName("이미 참여 중인 멤버가 중복 신청하면 ConflictException 발생")
        void throwsConflictWhenAlreadyApplied() {
            // given
            Gathering gathering = createGathering(RecruitType.FIRST_COME, GatheringStatus.RECRUITING, 4);
            // host(id=2)가 이미 멤버 + user(id=1)도 멤버로 추가
            Set<GatheringMember> members = new HashSet<>();
            members.add(new GatheringMember(host, gathering, true));
            members.add(new GatheringMember(user, gathering, false));
            ReflectionTestUtils.setField(gathering, "gatheringMembers", members);

            given(userService.findById(1L)).willReturn(user);
            given(gatheringRepository.findById(10L)).willReturn(Optional.of(gathering));

            // when & then
            assertThatThrownBy(() -> gatheringRequestService.applyGathering(10L, 1L, null))
                    .isInstanceOf(ConflictException.class);
        }

        @Test
        @DisplayName("선착순 동행모집에 정상 신청하면 멤버가 추가된다")
        void firstComeApplySuccess() {
            // given
            Gathering gathering = createGathering(RecruitType.FIRST_COME, GatheringStatus.RECRUITING, 4);
            given(userService.findById(1L)).willReturn(user);
            given(gatheringRepository.findById(10L)).willReturn(Optional.of(gathering));

            // when
            GatheringRequest result = gatheringRequestService.applyGathering(10L, 1L, null);

            // then
            assertThat(result).isNull(); // 선착순은 null 반환
            assertThat(gathering.getGatheringMembers()).hasSize(2);
        }

        @Test
        @DisplayName("승낙제 동행모집에 정상 신청하면 참가서가 생성된다")
        void confirmApplySuccess() {
            // given
            Gathering gathering = createGathering(RecruitType.CONFIRM, GatheringStatus.RECRUITING, 4);
            given(userService.findById(1L)).willReturn(user);
            given(gatheringRepository.findById(10L)).willReturn(Optional.of(gathering));
            given(gatheringRequestRepository.findAllByGathering(gathering)).willReturn(List.of());
            given(gatheringRequestRepository.save(any(GatheringRequest.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            GatheringRequest result = gatheringRequestService.applyGathering(10L, 1L, "참여 희망합니다");

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRequestUser()).isEqualTo(user);
            verify(gatheringNotificationService).sendGatheringRequestPush(eq(gathering), any(GatheringRequest.class));
        }
    }

    @Nested
    @DisplayName("confirmGatheringRequest")
    class ConfirmGatheringRequest {

        @Test
        @DisplayName("호스트가 아닌 사용자가 수락/거절하면 ForbiddenException 발생")
        void throwsForbiddenWhenNotHost() {
            // given
            Gathering gathering = createGathering(RecruitType.CONFIRM, GatheringStatus.RECRUITING, 4);
            GatheringRequest request = new GatheringRequest(user, gathering, GatheringRequestStatus.VERIFIED, "사유");
            ReflectionTestUtils.setField(request, "id", 100L);

            GatheringConfirmReqDTO dto = GatheringConfirmReqDTO.builder()
                    .gatheringRequestId(100L)
                    .gatheringRequestStatus(GatheringRequestStatus.APPROVAL)
                    .build();

            given(gatheringRequestRepository.findById(100L)).willReturn(Optional.of(request));

            // when & then (user.id=1, host.id=2 이므로 user가 호스트 아님)
            assertThatThrownBy(() -> gatheringRequestService.confirmGatheringRequest(dto, 1L))
                    .isInstanceOf(ForbiddenException.class);
        }

        @Test
        @DisplayName("이미 승인/거절된 참가서에 다시 처리하면 ConflictException 발생")
        void throwsConflictWhenAlreadyProcessed() {
            // given
            Gathering gathering = createGathering(RecruitType.CONFIRM, GatheringStatus.RECRUITING, 4);
            GatheringRequest request = new GatheringRequest(user, gathering, GatheringRequestStatus.APPROVAL, "사유");
            ReflectionTestUtils.setField(request, "id", 100L);

            GatheringConfirmReqDTO dto = GatheringConfirmReqDTO.builder()
                    .gatheringRequestId(100L)
                    .gatheringRequestStatus(GatheringRequestStatus.APPROVAL)
                    .build();

            given(gatheringRequestRepository.findById(100L)).willReturn(Optional.of(request));

            // when & then
            assertThatThrownBy(() -> gatheringRequestService.confirmGatheringRequest(dto, 2L))
                    .isInstanceOf(ConflictException.class);
        }
    }

    @Nested
    @DisplayName("cancelGatheringRequest")
    class CancelGatheringRequest {

        @Test
        @DisplayName("본인의 참가서가 아닌 경우 ForbiddenException 발생")
        void throwsForbiddenWhenNotOwner() {
            // given
            Gathering gathering = createGathering(RecruitType.CONFIRM, GatheringStatus.RECRUITING, 4);
            GatheringRequest request = new GatheringRequest(user, gathering, GatheringRequestStatus.UNVERIFIED, "사유");
            ReflectionTestUtils.setField(request, "id", 100L);

            GatheringCancelReqDTO dto = GatheringCancelReqDTO.builder()
                    .gatheringRequestId(100L)
                    .build();

            given(gatheringRequestRepository.findById(100L)).willReturn(Optional.of(request));

            // when & then (host.id=2로 취소 시도)
            assertThatThrownBy(() -> gatheringRequestService.cancelGatheringRequest(dto, 2L))
                    .isInstanceOf(ForbiddenException.class);
        }

        @Test
        @DisplayName("이미 승인된 참가서를 취소하면 BadRequestException 발생")
        void throwsBadRequestWhenAlreadyApproved() {
            // given
            Gathering gathering = createGathering(RecruitType.CONFIRM, GatheringStatus.RECRUITING, 4);
            GatheringRequest request = new GatheringRequest(user, gathering, GatheringRequestStatus.APPROVAL, "사유");
            ReflectionTestUtils.setField(request, "id", 100L);

            GatheringCancelReqDTO dto = GatheringCancelReqDTO.builder()
                    .gatheringRequestId(100L)
                    .build();

            given(gatheringRequestRepository.findById(100L)).willReturn(Optional.of(request));

            // when & then
            assertThatThrownBy(() -> gatheringRequestService.cancelGatheringRequest(dto, 1L))
                    .isInstanceOf(BadRequestException.class);
        }
    }
}
