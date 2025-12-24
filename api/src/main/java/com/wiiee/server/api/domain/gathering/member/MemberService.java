package com.wiiee.server.api.domain.gathering.member;

import com.wiiee.server.api.application.gathering.member.MemberModel;
import com.wiiee.server.api.domain.gathering.GatheringService;
import com.wiiee.server.api.domain.image.ImageService;
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
    private final ImageService imageService;

    @Transactional
    public MemberModel addMember(Long userId, Long gatheringId) {
        final var user = userService.findById(userId);
        final var gathering = gatheringService.findById(gatheringId);
        if (gathering.isContainUser(userId)) {
            throw new RuntimeException("멤버 등록이 불가능한 유저입니다.");
        }
        final var member = gathering.addMember(user);
        memberRepository.save(member);  // ID 생성을 위해 명시적으로 저장
        String profileImageUrl = imageService.getImageById(user.getProfile().getProfileImageId()).getUrl();
        return MemberModel.fromMember(member, profileImageUrl);
    }

    @Transactional
    public MemberModel updateMember(Long memberId, Integer statusCode) {
        final var member = memberRepository.findById(memberId).orElseThrow();
        member.updateStatus(statusCode);
        String profileImageUrl = imageService.getImageById(member.getUser().getProfile().getProfileImageId()).getUrl();
        return MemberModel.fromMember(member, profileImageUrl);
    }
}
