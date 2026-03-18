package com.wiiee.server.api.integration.service;

import com.wiiee.server.api.application.exception.ForbiddenException;
import com.wiiee.server.api.domain.gathering.GatheringRepository;
import com.wiiee.server.api.domain.gathering.GatheringService;
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

class GatheringServiceIntegrationTest extends ServiceIntegrationTest {

    @Autowired
    private GatheringService gatheringService;

    @Autowired
    private GatheringRepository gatheringRepository;

    @Autowired
    private EntityManager entityManager;

    private Long leaderId;
    private Long otherUserId;
    private Long contentId;

    @BeforeEach
    void setUpData() {
        transactionTemplate.executeWithoutResult(status -> {
            User leader = entityManager.merge(User.of("leader@test.com", "leader"));
            User otherUser = entityManager.merge(User.of("other@test.com", "other"));
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
            otherUserId = otherUser.getId();
            contentId = content.getId();
        });
    }

    private Gathering createGathering() {
        return transactionTemplate.execute(status -> {
            User leader = entityManager.find(User.class, leaderId);
            User otherUser = entityManager.find(User.class, otherUserId);
            Content content = entityManager.find(Content.class, contentId);
            GatheringInfo info = new GatheringInfo("test gathering", "information",
                    State.SEOUL, City.SONGPAGU, RecruitType.FIRST_COME, 4,
                    GenderType.IRRELEVANT, true, LocalDate.now().plusDays(7), "", new ArrayList<>());
            Gathering gathering = new Gathering(content, leader, info);
            gathering.addMember(leader);
            gathering.addMember(otherUser);
            return gatheringRepository.save(gathering);
        });
    }

    @Test
    @DisplayName("삭제 시 동행모집이 soft-delete 된다")
    void deleteGathering_softDeletes() {
        Gathering gathering = createGathering();
        Long gatheringId = gathering.getId();

        gatheringService.deleteGathering(gatheringId, leaderId);

        Gathering deleted = gatheringRepository.findById(gatheringId).orElseThrow();
        assertThat(deleted.getDeleted()).isTrue();
        assertThat(deleted.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("삭제 시 모든 멤버가 제거된다")
    void deleteGathering_removesAllMembers() {
        Gathering gathering = createGathering();
        Long gatheringId = gathering.getId();

        gatheringService.deleteGathering(gatheringId, leaderId);

        transactionTemplate.executeWithoutResult(status -> {
            Gathering deleted = gatheringRepository.findById(gatheringId).orElseThrow();
            assertThat(deleted.getGatheringMembers()).isEmpty();
        });
    }

    @Test
    @DisplayName("호스트가 아닌 사용자의 삭제 시도는 실패한다")
    void deleteGathering_notHost_throwsForbidden() {
        Gathering gathering = createGathering();

        assertThatThrownBy(() ->
                gatheringService.deleteGathering(gathering.getId(), otherUserId))
                .isInstanceOf(ForbiddenException.class);
    }
}
