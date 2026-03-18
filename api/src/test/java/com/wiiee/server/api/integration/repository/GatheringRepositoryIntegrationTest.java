package com.wiiee.server.api.integration.repository;

import com.wiiee.server.api.application.gathering.GatheringGetRequestDTO;
import com.wiiee.server.api.domain.gathering.GatheringRepository;
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
import com.wiiee.server.common.domain.gathering.member.GatheringMember;
import com.wiiee.server.common.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GatheringRepositoryIntegrationTest extends RepositoryIntegrationTest {

    @Autowired
    private GatheringRepository gatheringRepository;

    private User leader;
    private User member;
    private Company company;
    private Content content;

    @BeforeEach
    void setUp() {
        leader = tem.persistAndFlush(User.of("leader@test.com", "leader"));
        member = tem.persistAndFlush(User.of("member@test.com", "member"));

        AdminUser adminUser = tem.persistAndFlush(AdminUser.of("admin@test.com", "password"));
        company = tem.persistAndFlush(new Company(
                adminUser,
                new CompanyBasicInfo("TestCompany", State.SEOUL, City.SONGPAGU,
                        "addr", null, null, null, null, true,
                        List.of(1, 2, 3, 4, 5), true, new ArrayList<>()),
                null
        ));
        content = tem.persistAndFlush(new Content(company,
                new ContentBasicInfo("TestContent", Genre.THRILLER, "info", 60,
                        null, null, false, 2, 4, Difficulty.LEVEL3,
                        new ArrayList<>(), false, false, null, true)));
    }

    private Gathering createGathering(String title, RecruitType recruitType, Genre genre, Difficulty difficulty) {
        ContentBasicInfo cbi = new ContentBasicInfo("Content_" + title, genre, "info", 60,
                null, null, false, 2, 4, difficulty,
                new ArrayList<>(), false, false, null, true);
        Content c = tem.persistAndFlush(new Content(company, cbi));
        GatheringInfo info = new GatheringInfo(title, "information",
                State.SEOUL, City.SONGPAGU, recruitType, 4,
                GenderType.IRRELEVANT, true, LocalDate.now().plusDays(7), "", new ArrayList<>());
        Gathering gathering = new Gathering(c, leader, info);
        gathering.addMember(leader);
        return tem.persistAndFlush(gathering);
    }

    @Test
    @DisplayName("조건 없이 조회하면 삭제되지 않은 동행모집만 반환한다")
    void findAll_excludesDeleted() {
        Gathering active = createGathering("active", RecruitType.FIRST_COME, Genre.THRILLER, Difficulty.LEVEL1);
        Gathering deleted = createGathering("deleted", RecruitType.FIRST_COME, Genre.THRILLER, Difficulty.LEVEL1);
        deleted.delete(leader.getId());
        tem.flush();
        flushAndClear();

        GatheringGetRequestDTO dto = GatheringGetRequestDTO.builder().page(1).size(10).build();
        Page<Gathering> result = gatheringRepository.findAllByGatheringGetRequestDTO(dto, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getGatheringInfo().getTitle()).isEqualTo("active");
    }

    @Test
    @DisplayName("제목으로 검색하면 해당 제목을 포함하는 결과만 반환한다")
    void findAll_filterByTitle() {
        createGathering("escape room adventure", RecruitType.FIRST_COME, Genre.ADVENTURE, Difficulty.LEVEL1);
        createGathering("horror night", RecruitType.FIRST_COME, Genre.HORROR, Difficulty.LEVEL2);
        flushAndClear();

        GatheringGetRequestDTO dto = GatheringGetRequestDTO.builder()
                .page(1).size(10).title("escape").build();
        Page<Gathering> result = gatheringRepository.findAllByGatheringGetRequestDTO(dto, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getGatheringInfo().getTitle()).contains("escape");
    }

    @Test
    @DisplayName("난이도 코드로 필터링한다")
    void findAll_filterByDifficulty() {
        createGathering("easy", RecruitType.FIRST_COME, Genre.THRILLER, Difficulty.LEVEL1);
        createGathering("hard", RecruitType.FIRST_COME, Genre.THRILLER, Difficulty.LEVEL5);
        flushAndClear();

        GatheringGetRequestDTO dto = GatheringGetRequestDTO.builder()
                .page(1).size(10).difficultyCode(List.of(Difficulty.LEVEL1.getCode())).build();
        Page<Gathering> result = gatheringRepository.findAllByGatheringGetRequestDTO(dto, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getGatheringInfo().getTitle()).isEqualTo("easy");
    }

    @Test
    @DisplayName("장르 코드로 필터링한다")
    void findAll_filterByGenre() {
        createGathering("thriller1", RecruitType.FIRST_COME, Genre.THRILLER, Difficulty.LEVEL1);
        createGathering("adventure1", RecruitType.FIRST_COME, Genre.ADVENTURE, Difficulty.LEVEL1);
        flushAndClear();

        GatheringGetRequestDTO dto = GatheringGetRequestDTO.builder()
                .page(1).size(10).genreCode(Genre.ADVENTURE.getCode()).build();
        Page<Gathering> result = gatheringRepository.findAllByGatheringGetRequestDTO(dto, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getGatheringInfo().getTitle()).isEqualTo("adventure1");
    }

    @Test
    @DisplayName("페이지네이션이 정상 동작한다")
    void findAll_pagination() {
        for (int i = 0; i < 5; i++) {
            createGathering("gathering" + i, RecruitType.FIRST_COME, Genre.THRILLER, Difficulty.LEVEL1);
        }
        flushAndClear();

        GatheringGetRequestDTO dto = GatheringGetRequestDTO.builder().page(1).size(2).build();
        Page<Gathering> page1 = gatheringRepository.findAllByGatheringGetRequestDTO(dto, PageRequest.of(0, 2));

        assertThat(page1.getContent()).hasSize(2);
        assertThat(page1.getTotalElements()).isEqualTo(5);
        assertThat(page1.getTotalPages()).isEqualTo(3);
        assertThat(page1.hasNext()).isTrue();
    }

    @Test
    @DisplayName("count 쿼리가 fetch join 없이 동작한다")
    void findAll_countQueryWorks() {
        for (int i = 0; i < 3; i++) {
            createGathering("g" + i, RecruitType.FIRST_COME, Genre.THRILLER, Difficulty.LEVEL1);
        }
        flushAndClear();

        GatheringGetRequestDTO dto = GatheringGetRequestDTO.builder().page(1).size(1).build();
        Page<Gathering> result = gatheringRepository.findAllByGatheringGetRequestDTO(dto, PageRequest.of(0, 1));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    @DisplayName("승인된 멤버 수를 정확히 반환한다")
    void findCountByUser_returnsApprovedMemberCount() {
        createGathering("g1", RecruitType.FIRST_COME, Genre.THRILLER, Difficulty.LEVEL1);
        createGathering("g2", RecruitType.FIRST_COME, Genre.THRILLER, Difficulty.LEVEL2);
        flushAndClear();

        int count = gatheringRepository.findCountByUser(leader);

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("사용자가 참여한 동행모집만 반환한다")
    void findAllMyGathering_returnsOnlyMemberGatherings() {
        Gathering g1 = createGathering("myGathering", RecruitType.FIRST_COME, Genre.THRILLER, Difficulty.LEVEL1);
        createGathering("otherGathering", RecruitType.FIRST_COME, Genre.THRILLER, Difficulty.LEVEL2);

        g1.addMember(member);
        tem.flush();
        flushAndClear();

        List<Gathering> result = gatheringRepository.findAllMyGathering(member);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getGatheringInfo().getTitle()).isEqualTo("myGathering");
    }

    @Test
    @DisplayName("특정 동행모집의 특정 사용자 멤버를 반환한다")
    void findGatheringMember_returnsCorrectMember() {
        Gathering gathering = createGathering("test", RecruitType.FIRST_COME, Genre.THRILLER, Difficulty.LEVEL1);
        gathering.addMember(member);
        tem.flush();
        flushAndClear();

        Gathering reloaded = gatheringRepository.findById(gathering.getId()).orElseThrow();
        User reloadedMember = entityManager.find(User.class, member.getId());
        GatheringMember result = gatheringRepository.findGatheringMember(reloaded, reloadedMember);

        assertThat(result).isNotNull();
        assertThat(result.getUser().getId()).isEqualTo(member.getId());
    }
}
