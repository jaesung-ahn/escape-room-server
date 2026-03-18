package com.wiiee.server.api.integration.service;

import com.wiiee.server.api.application.exception.ForbiddenException;
import com.wiiee.server.api.application.exception.ResourceNotFoundException;
import com.wiiee.server.api.domain.gathering.GatheringMemberService;
import com.wiiee.server.api.domain.gathering.GatheringRepository;
import com.wiiee.server.api.domain.gathering.GatheringRequestRepository;
import com.wiiee.server.api.integration.ServiceIntegrationTest;
import com.wiiee.server.common.domain.admin.AdminUser;
import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.common.State;
import com.wiiee.server.common.domain.company.Company;
import com.wiiee.server.common.domain.company.CompanyBasicInfo;
import com.wiiee.server.common.domain.content.Content;
import com.wiiee.server.common.domain.content.ContentBasicInfo;
import com.wiiee.server.common.domain.content.Difficulty;
import com.wiiee.server.common.domain.content.Genre;
import com.wiiee.server.common.domain.gathering.Gathering;
import com.wiiee.server.common.domain.gathering.GatheringInfo;
import com.wiiee.server.common.domain.gathering.GenderType;
import com.wiiee.server.common.domain.gathering.RecruitType;
import com.wiiee.server.common.domain.gathering.request.GatheringRequest;
import com.wiiee.server.common.domain.gathering.request.GatheringRequestStatus;
import com.wiiee.server.common.domain.user.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GatheringMemberServiceIntegrationTest extends ServiceIntegrationTest {

    @Autowired
    private GatheringMemberService gatheringMemberService;

    @Autowired
    private GatheringRepository gatheringRepository;

    @Autowired
    private GatheringRequestRepository gatheringRequestRepository;

    @Autowired
    private EntityManager entityManager;

    private Long leaderId;
    private Long memberUserId;
    private Long nonMemberUserId;
    private Long contentId;

    @BeforeEach
    void setUpData() {
        transactionTemplate.executeWithoutResult(status -> {
            User leader = entityManager.merge(User.of("leader@test.com", "leader"));
            User memberUser = entityManager.merge(User.of("member@test.com", "member"));
            User nonMemberUser = entityManager.merge(User.of("nonmember@test.com", "nonmember"));
            AdminUser adminUser = entityManager.merge(AdminUser.of("admin@test.com", "password"));
            Company company = entityManager.merge(new Company(
                    adminUser,
                    new CompanyBasicInfo("TestCo", State.SEOUL, City.SONGPAGU,
                            "addr", null, null, null, null, true,
                            List.of(1, 2, 3, 4, 5), true, new ArrayList<>()),
                    null));
            Content content = entityManager.merge(new Content(company,
                    new ContentBasicInfo("TestContent", Genre.THRILLER, "info", 60,
                            null, null, false, 2, 4, Difficulty.LEVEL1,
                            new ArrayList<>(), false, false, null, true)));
            entityManager.flush();
            leaderId = leader.getId();
            memberUserId = memberUser.getId();
            nonMemberUserId = nonMemberUser.getId();
            contentId = content.getId();
        });
    }

    private Gathering createGatheringWithMemberAndApprovedRequest() {
        return transactionTemplate.execute(status -> {
            User leader = entityManager.find(User.class, leaderId);
            User memberUser = entityManager.find(User.class, memberUserId);
            Content content = entityManager.find(Content.class, contentId);
            GatheringInfo info = new GatheringInfo("test gathering", "information",
                    State.SEOUL, City.SONGPAGU, RecruitType.CONFIRM, 4,
                    GenderType.IRRELEVANT, true, LocalDate.now().plusDays(7), "", new ArrayList<>());
            Gathering gathering = new Gathering(content, leader, info);
            gathering.addMember(leader);
            gathering.addMember(memberUser);
            Gathering saved = gatheringRepository.save(gathering);

            GatheringRequest approvedRequest = new GatheringRequest(
                    memberUser, saved, GatheringRequestStatus.APPROVAL, "approved");
            gatheringRequestRepository.save(approvedRequest);

            return saved;
        });
    }

    @Test
    @DisplayName("참여 취소 시 멤버가 삭제되고 참가서 상태가 변경된다")
    void cancelJoinGathering_removesMemberAndUpdatesRequest() {
        Gathering gathering = createGatheringWithMemberAndApprovedRequest();

        gatheringMemberService.cancelJoinGathering(gathering.getId(), memberUserId);

        transactionTemplate.executeWithoutResult(status -> {
            Gathering reloaded = gatheringRepository.findById(gathering.getId()).orElseThrow();
            assertThat(reloaded.isContainUser(memberUserId)).isFalse();
        });
    }

    @Test
    @DisplayName("참여하지 않은 사용자의 취소 시도는 실패한다")
    void cancelJoinGathering_notMember_throwsForbidden() {
        Gathering gathering = createGatheringWithMemberAndApprovedRequest();

        assertThatThrownBy(() ->
                gatheringMemberService.cancelJoinGathering(gathering.getId(), nonMemberUserId))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("승인된 참가서가 없는 경우 취소 시도는 실패한다")
    void cancelJoinGathering_noApprovedRequest_throwsNotFound() {
        Gathering gathering = transactionTemplate.execute(status -> {
            User leader = entityManager.find(User.class, leaderId);
            User memberUser = entityManager.find(User.class, memberUserId);
            Content content = entityManager.find(Content.class, contentId);
            GatheringInfo info = new GatheringInfo("test", "information",
                    State.SEOUL, City.SONGPAGU, RecruitType.FIRST_COME, 4,
                    GenderType.IRRELEVANT, true, LocalDate.now().plusDays(7), "", new ArrayList<>());
            Gathering g = new Gathering(content, leader, info);
            g.addMember(leader);
            g.addMember(memberUser);
            return gatheringRepository.save(g);
        });

        assertThatThrownBy(() ->
                gatheringMemberService.cancelJoinGathering(gathering.getId(), memberUserId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
