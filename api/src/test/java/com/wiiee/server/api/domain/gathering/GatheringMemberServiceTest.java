package com.wiiee.server.api.domain.gathering;

import com.wiiee.server.api.application.exception.ForbiddenException;
import com.wiiee.server.api.application.exception.ResourceNotFoundException;
import com.wiiee.server.api.application.gathering.member.WaitingMemberModel;
import com.wiiee.server.api.domain.image.ImageService;
import com.wiiee.server.api.domain.user.UserService;
import com.wiiee.server.common.domain.common.Image;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("GatheringMemberService 단위 테스트")
class GatheringMemberServiceTest {

    @Mock
    private GatheringRepository gatheringRepository;
    @Mock
    private GatheringRequestRepository gatheringRequestRepository;
    @Mock
    private UserService userService;
    @Mock
    private ImageService imageService;

    @InjectMocks
    private GatheringMemberService gatheringMemberService;

    private User host;
    private User member;
    private Content content;

    @BeforeEach
    void setUp() {
        host = new FixtureUser("host@test.com", "호스트");
        member = new FixtureUser("member@test.com", "멤버");
        ReflectionTestUtils.setField(member, "id", 2L);
        content = new Content(null, new ContentBasicInfo("놀거리"));
        ReflectionTestUtils.setField(content, "id", 1L);
    }

    private Gathering createGathering() {
        GatheringInfo info = new GatheringInfo("동행", "설명", State.SEOUL, City.GANGNAMGU,
                RecruitType.CONFIRM, 4, GenderType.IRRELEVANT, true,
                LocalDate.now().plusDays(7), "", GatheringStatus.RECRUITING);
        Gathering gathering = new Gathering(content, host, info);
        ReflectionTestUtils.setField(gathering, "id", 10L);
        Set<GatheringMember> members = new HashSet<>();
        GatheringMember hostMember = new GatheringMember(host, gathering, true);
        members.add(hostMember);
        GatheringMember memberEntry = new GatheringMember(member, gathering, false);
        members.add(memberEntry);
        ReflectionTestUtils.setField(gathering, "gatheringMembers", members);
        return gathering;
    }

    @Nested
    @DisplayName("cancelJoinGathering")
    class CancelJoinGathering {

        @Test
        @DisplayName("참여하지 않은 멤버가 취소하면 ForbiddenException 발생")
        void throwsForbiddenWhenNotMember() {
            // given
            Gathering gathering = createGathering();
            User nonMember = new FixtureUser("non@test.com", "비멤버");
            ReflectionTestUtils.setField(nonMember, "id", 3L);

            given(gatheringRepository.findById(10L)).willReturn(Optional.of(gathering));
            given(userService.findById(3L)).willReturn(nonMember);
            given(gatheringRepository.findGatheringMember(gathering, nonMember)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> gatheringMemberService.cancelJoinGathering(10L, 3L))
                    .isInstanceOf(ForbiddenException.class);
        }

        @Test
        @DisplayName("승인된 참가서가 없으면 ResourceNotFoundException 발생")
        void throwsNotFoundWhenNoApprovedRequest() {
            // given
            Gathering gathering = createGathering();
            GatheringMember gatheringMember = new GatheringMember(member, gathering, false);

            given(gatheringRepository.findById(10L)).willReturn(Optional.of(gathering));
            given(userService.findById(2L)).willReturn(member);
            given(gatheringRepository.findGatheringMember(gathering, member)).willReturn(gatheringMember);
            given(gatheringRequestRepository.findApprovedGatheringRequest(gathering, member)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> gatheringMemberService.cancelJoinGathering(10L, 2L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("정상적으로 참여를 취소한다")
        void cancelSuccessfully() {
            // given
            Gathering gathering = createGathering();
            GatheringMember gatheringMember = new GatheringMember(member, gathering, false);
            GatheringRequest request = new GatheringRequest(member, gathering, GatheringRequestStatus.APPROVAL, "사유");

            given(gatheringRepository.findById(10L)).willReturn(Optional.of(gathering));
            given(userService.findById(2L)).willReturn(member);
            given(gatheringRepository.findGatheringMember(gathering, member)).willReturn(gatheringMember);
            given(gatheringRequestRepository.findApprovedGatheringRequest(gathering, member)).willReturn(request);

            // when
            gatheringMemberService.cancelJoinGathering(10L, 2L);

            // then
            assertThat(request.getGatheringRequestStatus()).isEqualTo(GatheringRequestStatus.CANCELED_JOIN);
        }
    }

    @Nested
    @DisplayName("getWaitingMember")
    class GetWaitingMember {

        @Test
        @DisplayName("프로필 이미지를 배치 조회하여 대기 멤버 목록을 반환한다")
        void batchImageLoadForWaitingMembers() {
            // given
            Gathering gathering = createGathering();
            ReflectionTestUtils.setField(member.getProfile(), "profileImageId", 100L);

            GatheringRequest request = new GatheringRequest(member, gathering, GatheringRequestStatus.UNVERIFIED, "참여 희망");
            ReflectionTestUtils.setField(request, "id", 1L);

            Image profileImage = new Image("profile-url");
            ReflectionTestUtils.setField(profileImage, "id", 100L);

            given(gatheringRequestRepository.findAllByGathering(gathering)).willReturn(List.of(request));
            given(imageService.findByIdsIn(anyList())).willReturn(List.of(profileImage));

            // when
            List<WaitingMemberModel> result = gatheringMemberService.getWaitingMember(gathering);

            // then
            assertThat(result).hasSize(1);
        }
    }
}
