package com.wiiee.server.api.domain.gathering.member;

import com.wiiee.server.api.application.gathering.member.MemberModel;
import com.wiiee.server.api.domain.gathering.GatheringService;
import com.wiiee.server.api.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final GatheringService gatheringService;
    private final UserService userService;

    @Transactional
    public MemberModel addMember(Long userId, Long gatheringId) {
        final var user = userService.findById(userId);
        final var gathering = gatheringService.findById(gatheringId);
        if (gathering.isContainUser(userId)) {
            throw new RuntimeException("멤버 등록이 불가능한 유저입니다.");
        }
        return MemberModel.fromMember(gathering.addMember(user));
    }

    @Transactional
    public MemberModel updateMember(Long memberId, Integer statusCode) {
        final var member = memberRepository.findById(memberId).orElseThrow();
        member.updateStatus(statusCode);
        return MemberModel.fromMember(member);
    }
}
