package com.wiiee.server.api.domain.gathering;

import com.wiiee.server.api.application.exception.BadRequestException;
import com.wiiee.server.api.application.exception.ForbiddenException;
import com.wiiee.server.api.application.exception.ResourceNotFoundException;
import com.wiiee.server.api.application.gathering.GatheringUpdateRequestDTO;
import com.wiiee.server.api.domain.content.ContentService;
import com.wiiee.server.api.domain.gathering.comment.CommentService;
import com.wiiee.server.api.domain.image.ImageService;
import com.wiiee.server.api.domain.user.UserService;
import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.common.State;
import com.wiiee.server.common.domain.content.Content;
import com.wiiee.server.common.domain.content.ContentBasicInfo;
import com.wiiee.server.common.domain.gathering.*;
import com.wiiee.server.common.domain.gathering.member.GatheringMember;
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
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("GatheringService 단위 테스트")
class GatheringServiceTest {

    @Mock
    private GatheringRepository gatheringRepository;
    @Mock
    private GatheringRequestRepository gatheringRequestRepository;
    @Mock
    private UserService userService;
    @Mock
    private ContentService contentService;
    @Mock
    private ImageService imageService;
    @Mock
    private CommentService commentService;
    @Mock
    private GatheringMemberService gatheringMemberService;

    @InjectMocks
    private GatheringService gatheringService;

    private User host;
    private User otherUser;
    private Content content;

    @BeforeEach
    void setUp() {
        host = new FixtureUser("host@test.com", "호스트");
        ReflectionTestUtils.setField(host, "id", 1L);
        otherUser = new FixtureUser("other@test.com", "다른사용자");
        ReflectionTestUtils.setField(otherUser, "id", 2L);
        content = new Content(null, new ContentBasicInfo("테스트 놀거리"));
        ReflectionTestUtils.setField(content, "id", 1L);
    }

    private Gathering createGathering(int maxPeople) {
        GatheringInfo info = new GatheringInfo("테스트 동행", "설명", State.SEOUL, City.GANGNAMGU,
                RecruitType.FIRST_COME, maxPeople, GenderType.IRRELEVANT, true,
                LocalDate.now().plusDays(7), "", GatheringStatus.RECRUITING);
        Gathering gathering = new Gathering(content, host, info);
        ReflectionTestUtils.setField(gathering, "id", 10L);
        Set<GatheringMember> members = new HashSet<>();
        members.add(new GatheringMember(host, gathering, true));
        ReflectionTestUtils.setField(gathering, "gatheringMembers", members);
        ReflectionTestUtils.setField(gathering, "deleted", false);
        return gathering;
    }

    @Nested
    @DisplayName("updateGathering")
    class UpdateGathering {

        @Test
        @DisplayName("호스트가 아닌 사용자가 수정하면 ForbiddenException 발생")
        void throwsForbiddenWhenNotHost() {
            // given
            Gathering gathering = createGathering(4);
            given(gatheringRepository.findById(10L)).willReturn(Optional.of(gathering));

            GatheringUpdateRequestDTO dto = new GatheringUpdateRequestDTO();
            ReflectionTestUtils.setField(dto, "gatheringId", 10L);

            // when & then
            assertThatThrownBy(() -> gatheringService.updateGathering(dto, 2L))
                    .isInstanceOf(ForbiddenException.class);
        }

        @Test
        @DisplayName("과거 일자로 수정하면 BadRequestException 발생")
        void throwsBadRequestWhenPastDate() {
            // given
            Gathering gathering = createGathering(4);
            given(gatheringRepository.findById(10L)).willReturn(Optional.of(gathering));

            GatheringUpdateRequestDTO dto = new GatheringUpdateRequestDTO();
            ReflectionTestUtils.setField(dto, "gatheringId", 10L);
            ReflectionTestUtils.setField(dto, "hopeDate", LocalDate.now().minusDays(1));

            // when & then
            assertThatThrownBy(() -> gatheringService.updateGathering(dto, 1L))
                    .isInstanceOf(BadRequestException.class);
        }

        @Test
        @DisplayName("현재 멤버 수보다 적은 최대 인원으로 수정하면 BadRequestException 발생")
        void throwsBadRequestWhenMaxPeopleLessThanCurrent() {
            // given
            Gathering gathering = createGathering(4);
            // 멤버 2명 추가 (총 3명)
            Set<GatheringMember> members = new HashSet<>(gathering.getGatheringMembers());
            members.add(new GatheringMember(otherUser, gathering, false));
            User thirdUser = new FixtureUser("third@test.com", "셋째");
            ReflectionTestUtils.setField(thirdUser, "id", 3L);
            members.add(new GatheringMember(thirdUser, gathering, false));
            ReflectionTestUtils.setField(gathering, "gatheringMembers", members);

            given(gatheringRepository.findById(10L)).willReturn(Optional.of(gathering));

            GatheringUpdateRequestDTO dto = new GatheringUpdateRequestDTO();
            ReflectionTestUtils.setField(dto, "gatheringId", 10L);
            ReflectionTestUtils.setField(dto, "hopeDate", LocalDate.now().plusDays(3));
            ReflectionTestUtils.setField(dto, "maxPeople", 2); // 현재 3명인데 2명으로

            // when & then
            assertThatThrownBy(() -> gatheringService.updateGathering(dto, 1L))
                    .isInstanceOf(BadRequestException.class);
        }
    }

    @Nested
    @DisplayName("getGatheringDetail")
    class GetGatheringDetail {

        @Test
        @DisplayName("삭제된 동행모집을 조회하면 ResourceNotFoundException 발생")
        void throwsNotFoundWhenDeleted() {
            // given
            Gathering gathering = createGathering(4);
            ReflectionTestUtils.setField(gathering, "deleted", true);
            given(gatheringRepository.findById(10L)).willReturn(Optional.of(gathering));

            // when & then
            assertThatThrownBy(() -> gatheringService.getGatheringDetail(10L, 1L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteGathering")
    class DeleteGathering {

        @Test
        @DisplayName("삭제 시 멤버, 댓글, 동행모집이 모두 삭제된다")
        void deleteCascadesAllRelated() {
            // given
            Gathering gathering = createGathering(4);
            given(gatheringRepository.findById(10L)).willReturn(Optional.of(gathering));

            // when
            gatheringService.deleteGathering(10L, 1L);

            // then
            verify(gatheringMemberService).removeAllMembers(gathering);
            verify(commentService).deleteAllByGatheringId(10L);
        }
    }

    @Nested
    @DisplayName("completedGathering")
    class CompletedGathering {

        @Test
        @DisplayName("호스트가 아닌 사용자가 완료 처리하면 ForbiddenException 발생")
        void throwsForbiddenWhenNotHost() {
            // given
            Gathering gathering = createGathering(4);
            given(gatheringRepository.findById(10L)).willReturn(Optional.of(gathering));

            // when & then
            assertThatThrownBy(() -> gatheringService.completedGathering(10L, 2L))
                    .isInstanceOf(ForbiddenException.class);
        }
    }
}
