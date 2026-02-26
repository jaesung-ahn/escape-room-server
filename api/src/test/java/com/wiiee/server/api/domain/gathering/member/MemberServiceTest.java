package com.wiiee.server.api.domain.gathering.member;

import com.wiiee.server.api.application.exception.ConflictException;
import com.wiiee.server.api.domain.gathering.GatheringService;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService 단위 테스트")
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private GatheringService gatheringService;
    @Mock
    private UserService userService;
    @Mock
    private ImageService imageService;

    @InjectMocks
    private MemberService memberService;

    private User host;
    private User existingMember;
    private Gathering gathering;

    @BeforeEach
    void setUp() {
        host = new FixtureUser("host@test.com", "호스트");
        existingMember = new FixtureUser("member@test.com", "기존멤버");
        ReflectionTestUtils.setField(existingMember, "id", 2L);

        Content content = new Content(null, new ContentBasicInfo("놀거리"));
        GatheringInfo info = new GatheringInfo("동행", "설명", State.SEOUL, City.GANGNAMGU,
                RecruitType.FIRST_COME, 4, GenderType.IRRELEVANT, true,
                LocalDate.now().plusDays(7), "", GatheringStatus.RECRUITING);
        gathering = new Gathering(content, host, info);
        ReflectionTestUtils.setField(gathering, "id", 10L);

        Set<GatheringMember> members = new HashSet<>();
        members.add(new GatheringMember(host, gathering, true));
        members.add(new GatheringMember(existingMember, gathering, false));
        ReflectionTestUtils.setField(gathering, "gatheringMembers", members);
    }

    @Test
    @DisplayName("이미 참여 중인 사용자가 다시 참여하면 ConflictException 발생")
    void throwsConflictWhenAlreadyMember() {
        // given
        given(userService.findById(2L)).willReturn(existingMember);
        given(gatheringService.findById(10L)).willReturn(gathering);

        // when & then
        assertThatThrownBy(() -> memberService.addMember(2L, 10L))
                .isInstanceOf(ConflictException.class);
    }
}
