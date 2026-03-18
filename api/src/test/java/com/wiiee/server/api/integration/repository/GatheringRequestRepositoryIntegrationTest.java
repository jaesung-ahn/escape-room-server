package com.wiiee.server.api.integration.repository;

import com.wiiee.server.api.domain.gathering.GatheringRequestRepository;
import com.wiiee.server.api.integration.RepositoryIntegrationTest;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GatheringRequestRepositoryIntegrationTest extends RepositoryIntegrationTest {

    @Autowired
    private GatheringRequestRepository gatheringRequestRepository;

    private User leader;
    private User applicant1;
    private User applicant2;
    private Gathering gathering;

    @BeforeEach
    void setUp() {
        leader = tem.persistAndFlush(User.of("leader@test.com", "leader"));
        applicant1 = tem.persistAndFlush(User.of("applicant1@test.com", "applicant1"));
        applicant2 = tem.persistAndFlush(User.of("applicant2@test.com", "applicant2"));

        AdminUser adminUser = tem.persistAndFlush(AdminUser.of("admin@test.com", "password"));
        Company company = tem.persistAndFlush(new Company(
                adminUser,
                new CompanyBasicInfo("TestCo", State.SEOUL, City.SONGPAGU,
                        "addr", null, null, null, null, true,
                        List.of(1, 2, 3, 4, 5), true, new ArrayList<>()),
                null));
        Content content = tem.persistAndFlush(new Content(company,
                new ContentBasicInfo("TestContent", Genre.THRILLER, "info", 60,
                        null, null, false, 2, 4, Difficulty.LEVEL1,
                        new ArrayList<>(), false, false, null, true)));

        GatheringInfo info = new GatheringInfo("test gathering", "information",
                State.SEOUL, City.SONGPAGU, RecruitType.CONFIRM, 4,
                GenderType.IRRELEVANT, true, LocalDate.now().plusDays(7), "", new ArrayList<>());
        gathering = new Gathering(content, leader, info);
        gathering.addMember(leader);
        gathering = tem.persistAndFlush(gathering);
    }

    private GatheringRequest createRequest(User user, GatheringRequestStatus status) {
        GatheringRequest request = new GatheringRequest(user, gathering, status, "request reason");
        return tem.persistAndFlush(request);
    }

    @Test
    @DisplayName("UNVERIFIED와 VERIFIED 상태의 참가서만 반환한다")
    void findAllByGathering_returnsOnlyUnverifiedAndVerified() {
        createRequest(applicant1, GatheringRequestStatus.UNVERIFIED);
        createRequest(applicant2, GatheringRequestStatus.VERIFIED);

        User rejected = tem.persistAndFlush(User.of("rejected@test.com", "rejected"));
        createRequest(rejected, GatheringRequestStatus.REJECT);
        flushAndClear();

        Gathering reloaded = entityManager.find(Gathering.class, gathering.getId());
        List<GatheringRequest> result = gatheringRequestRepository.findAllByGathering(reloaded);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(r ->
                r.getGatheringRequestStatus() == GatheringRequestStatus.UNVERIFIED ||
                        r.getGatheringRequestStatus() == GatheringRequestStatus.VERIFIED);
    }

    @Test
    @DisplayName("최신순으로 정렬한다")
    void findAllByGathering_orderedByCreatedAtDesc() {
        createRequest(applicant1, GatheringRequestStatus.UNVERIFIED);
        createRequest(applicant2, GatheringRequestStatus.UNVERIFIED);
        flushAndClear();

        Gathering reloaded = entityManager.find(Gathering.class, gathering.getId());
        List<GatheringRequest> result = gatheringRequestRepository.findAllByGathering(reloaded);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCreatedAt())
                .isAfterOrEqualTo(result.get(1).getCreatedAt());
    }

    @Test
    @DisplayName("UNVERIFIED 상태 참가서 수를 반환한다")
    void countUnverifiedRequestByGathering() {
        createRequest(applicant1, GatheringRequestStatus.UNVERIFIED);
        createRequest(applicant2, GatheringRequestStatus.UNVERIFIED);

        User verified = tem.persistAndFlush(User.of("verified@test.com", "verified"));
        createRequest(verified, GatheringRequestStatus.VERIFIED);
        flushAndClear();

        Gathering reloaded = entityManager.find(Gathering.class, gathering.getId());
        long count = gatheringRequestRepository.countUnverifiedRequestByGathering(reloaded);

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("승인된 참가서를 반환한다")
    void findApprovedGatheringRequest_returnsApproved() {
        createRequest(applicant1, GatheringRequestStatus.APPROVAL);
        flushAndClear();

        Gathering reloaded = entityManager.find(Gathering.class, gathering.getId());
        User reloadedApplicant = entityManager.find(User.class, applicant1.getId());
        GatheringRequest result = gatheringRequestRepository.findApprovedGatheringRequest(reloaded, reloadedApplicant);

        assertThat(result).isNotNull();
        assertThat(result.getGatheringRequestStatus()).isEqualTo(GatheringRequestStatus.APPROVAL);
        assertThat(result.getRequestUser().getId()).isEqualTo(applicant1.getId());
    }

    @Test
    @DisplayName("해당 조건의 참가서가 없으면 null을 반환한다")
    void findApprovedGatheringRequest_returnsNullWhenNotFound() {
        createRequest(applicant1, GatheringRequestStatus.UNVERIFIED);
        flushAndClear();

        Gathering reloaded = entityManager.find(Gathering.class, gathering.getId());
        User reloadedApplicant = entityManager.find(User.class, applicant1.getId());
        GatheringRequest result = gatheringRequestRepository.findApprovedGatheringRequest(reloaded, reloadedApplicant);

        assertThat(result).isNull();
    }
}
