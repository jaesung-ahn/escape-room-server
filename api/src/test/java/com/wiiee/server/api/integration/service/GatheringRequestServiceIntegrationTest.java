package com.wiiee.server.api.integration.service;

import com.wiiee.server.api.application.exception.ConflictException;
import com.wiiee.server.api.domain.gathering.GatheringRepository;
import com.wiiee.server.api.domain.gathering.GatheringRequestService;
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

class GatheringRequestServiceIntegrationTest extends ServiceIntegrationTest {

    @Autowired
    private GatheringRequestService gatheringRequestService;

    @Autowired
    private GatheringRepository gatheringRepository;

    @Autowired
    private EntityManager entityManager;

    private Long leaderId;
    private Long applicantId;
    private Long contentId;

    @BeforeEach
    void setUpData() {
        transactionTemplate.executeWithoutResult(status -> {
            User leader = entityManager.merge(User.of("leader@test.com", "leader"));
            User applicant = entityManager.merge(User.of("applicant@test.com", "applicant"));
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
            applicantId = applicant.getId();
            contentId = content.getId();
        });
    }

    private Gathering createGathering(RecruitType recruitType, int maxPeople) {
        return transactionTemplate.execute(status -> {
            User leader = entityManager.find(User.class, leaderId);
            Content content = entityManager.find(Content.class, contentId);
            GatheringInfo info = new GatheringInfo("test gathering", "information",
                    State.SEOUL, City.SONGPAGU, recruitType, maxPeople,
                    GenderType.IRRELEVANT, true, LocalDate.now().plusDays(7), "", new ArrayList<>());
            Gathering gathering = new Gathering(content, leader, info);
            gathering.addMember(leader);
            return gatheringRepository.save(gathering);
        });
    }

    @Test
    @DisplayName("선착순 동행모집에 신청하면 즉시 멤버로 추가된다")
    void applyGathering_firstCome_addsMemberDirectly() {
        Gathering gathering = createGathering(RecruitType.FIRST_COME, 4);

        gatheringRequestService.applyGathering(gathering.getId(), applicantId, "join");

        transactionTemplate.executeWithoutResult(status -> {
            Gathering reloaded = gatheringRepository.findById(gathering.getId()).orElseThrow();
            assertThat(reloaded.getGatheringMembers()).hasSize(2);
            assertThat(reloaded.isContainUser(applicantId)).isTrue();
        });
    }

    @Test
    @DisplayName("최대 인원 도달 시 멤버 추가를 거부한다")
    void applyGathering_maxReached_throwsConflict() {
        Gathering gathering = createGathering(RecruitType.FIRST_COME, 1);

        assertThatThrownBy(() ->
                gatheringRequestService.applyGathering(gathering.getId(), applicantId, "join"))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("승낙제 동행모집에 신청하면 참가서가 저장된다")
    void applyGathering_confirm_savesRequest() {
        Gathering gathering = createGathering(RecruitType.CONFIRM, 4);

        GatheringRequest result = gatheringRequestService.applyGathering(
                gathering.getId(), applicantId, "want to join");

        assertThat(result).isNotNull();
        assertThat(result.getGatheringRequestStatus()).isEqualTo(GatheringRequestStatus.UNVERIFIED);
        assertThat(result.getRequestUser().getId()).isEqualTo(applicantId);
    }

    @Test
    @DisplayName("동일 사용자 중복 신청 시 ConflictException 발생한다")
    void applyGathering_duplicateRequest_throwsConflict() {
        Gathering gathering = createGathering(RecruitType.CONFIRM, 4);
        gatheringRequestService.applyGathering(gathering.getId(), applicantId, "first");

        assertThatThrownBy(() ->
                gatheringRequestService.applyGathering(gathering.getId(), applicantId, "second"))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("이미 멤버인 사용자가 신청하면 ConflictException 발생한다")
    void applyGathering_alreadyMember_throwsConflict() {
        Gathering gathering = createGathering(RecruitType.FIRST_COME, 4);
        gatheringRequestService.applyGathering(gathering.getId(), applicantId, "join");

        assertThatThrownBy(() ->
                gatheringRequestService.applyGathering(gathering.getId(), applicantId, "again"))
                .isInstanceOf(ConflictException.class);
    }
}
