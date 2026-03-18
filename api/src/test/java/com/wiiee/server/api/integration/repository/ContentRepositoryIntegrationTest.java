package com.wiiee.server.api.integration.repository;

import com.wiiee.server.api.application.content.ContentGetRequestDTO;
import com.wiiee.server.api.domain.content.ContentRepository;
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
import com.wiiee.server.common.domain.content.RankContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ContentRepositoryIntegrationTest extends RepositoryIntegrationTest {

    @Autowired
    private ContentRepository contentRepository;

    private Company company;
    private Company company2;

    @BeforeEach
    void setUp() {
        AdminUser adminUser = tem.persistAndFlush(AdminUser.of("admin@test.com", "password"));
        company = tem.persistAndFlush(new Company(
                adminUser,
                new CompanyBasicInfo("CompanyA", State.SEOUL, City.SONGPAGU,
                        "addr", null, null, null, null, true,
                        List.of(1, 2, 3, 4, 5), true, new ArrayList<>()),
                null));
        company2 = tem.persistAndFlush(new Company(
                adminUser,
                new CompanyBasicInfo("CompanyB", State.BUSAN, City.BUSANJINGU,
                        "addr2", null, null, null, null, true,
                        List.of(1, 2, 3, 4, 5), true, new ArrayList<>()),
                null));
    }

    private Content createContent(String name, Company comp, Genre genre, Difficulty difficulty, boolean isOperated) {
        ContentBasicInfo cbi = new ContentBasicInfo(name, genre, "info", 60,
                null, null, false, 2, 4, difficulty,
                new ArrayList<>(), false, false, null, isOperated);
        return tem.persistAndFlush(new Content(comp, cbi));
    }

    @Test
    @DisplayName("운영 중인 놀거리만 반환한다")
    void findAll_onlyOperated() {
        createContent("operated", company, Genre.THRILLER, Difficulty.LEVEL1, true);
        createContent("notOperated", company, Genre.THRILLER, Difficulty.LEVEL1, false);
        flushAndClear();

        ContentGetRequestDTO dto = ContentGetRequestDTO.builder().page(1).size(10).build();
        Page<Content> result = contentRepository.findAllByContentGetRequestDTO(dto, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getContentBasicInfo().getName()).isEqualTo("operated");
    }

    @Test
    @DisplayName("업체 ID로 필터링한다")
    void findAll_filterByCompanyId() {
        createContent("contentA", company, Genre.THRILLER, Difficulty.LEVEL1, true);
        createContent("contentB", company2, Genre.THRILLER, Difficulty.LEVEL1, true);
        flushAndClear();

        ContentGetRequestDTO dto = ContentGetRequestDTO.builder()
                .page(1).size(10).companyId(company.getId()).build();
        Page<Content> result = contentRepository.findAllByContentGetRequestDTO(dto, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getContentBasicInfo().getName()).isEqualTo("contentA");
    }

    @Test
    @DisplayName("이름으로 검색한다")
    void findAll_filterByName() {
        createContent("escape room", company, Genre.THRILLER, Difficulty.LEVEL1, true);
        createContent("horror house", company, Genre.HORROR, Difficulty.LEVEL2, true);
        flushAndClear();

        ContentGetRequestDTO dto = ContentGetRequestDTO.builder()
                .page(1).size(10).name("escape").build();
        Page<Content> result = contentRepository.findAllByContentGetRequestDTO(dto, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getContentBasicInfo().getName()).contains("escape");
    }

    @Test
    @DisplayName("지역으로 필터링한다")
    void findAll_filterByState() {
        createContent("seoulContent", company, Genre.THRILLER, Difficulty.LEVEL1, true);
        createContent("busanContent", company2, Genre.THRILLER, Difficulty.LEVEL1, true);
        flushAndClear();

        ContentGetRequestDTO dto = ContentGetRequestDTO.builder()
                .page(1).size(10).stateCode(State.SEOUL.getCode()).build();
        Page<Content> result = contentRepository.findAllByContentGetRequestDTO(dto, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getContentBasicInfo().getName()).isEqualTo("seoulContent");
    }

    @Test
    @DisplayName("복수 장르 코드로 필터링한다")
    void findAll_filterByMultipleGenres() {
        createContent("thriller", company, Genre.THRILLER, Difficulty.LEVEL1, true);
        createContent("adventure", company, Genre.ADVENTURE, Difficulty.LEVEL1, true);
        createContent("horror", company, Genre.HORROR, Difficulty.LEVEL1, true);
        flushAndClear();

        ContentGetRequestDTO dto = ContentGetRequestDTO.builder()
                .page(1).size(10)
                .genreCodes(List.of(Genre.THRILLER.getCode(), Genre.ADVENTURE.getCode()))
                .build();
        Page<Content> result = contentRepository.findAllByContentGetRequestDTO(dto, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("페이지네이션과 count 쿼리가 정상 동작한다")
    void findAll_paginationAndCount() {
        for (int i = 0; i < 5; i++) {
            createContent("content" + i, company, Genre.THRILLER, Difficulty.LEVEL1, true);
        }
        flushAndClear();

        ContentGetRequestDTO dto = ContentGetRequestDTO.builder().page(1).size(2).build();
        Page<Content> result = contentRepository.findAllByContentGetRequestDTO(dto, PageRequest.of(0, 2));

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getTotalPages()).isEqualTo(3);
        assertThat(result.hasNext()).isTrue();
    }

    @Test
    @DisplayName("랭크 순서대로 인기 놀거리를 반환한다")
    void findAllMainHotContent_orderedByRank() {
        Content c1 = createContent("hot1", company, Genre.THRILLER, Difficulty.LEVEL1, true);
        Content c2 = createContent("hot2", company, Genre.ADVENTURE, Difficulty.LEVEL2, true);
        Content c3 = createContent("hot3", company, Genre.HORROR, Difficulty.LEVEL3, true);

        tem.persistAndFlush(new RankContent(1, c2));
        tem.persistAndFlush(new RankContent(2, c1));
        tem.persistAndFlush(new RankContent(3, c3));
        flushAndClear();

        List<RankContent> result = contentRepository.findAllMainHotContent(10);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getContent().getContentBasicInfo().getName()).isEqualTo("hot2");
        assertThat(result.get(1).getContent().getContentBasicInfo().getName()).isEqualTo("hot1");
        assertThat(result.get(2).getContent().getContentBasicInfo().getName()).isEqualTo("hot3");
    }

    @Test
    @DisplayName("비운영 놀거리를 제외한다")
    void findAllMainHotContent_excludesNotOperated() {
        Content operated = createContent("operated", company, Genre.THRILLER, Difficulty.LEVEL1, true);
        Content notOperated = createContent("notOperated", company, Genre.THRILLER, Difficulty.LEVEL1, false);

        tem.persistAndFlush(new RankContent(1, operated));
        tem.persistAndFlush(new RankContent(2, notOperated));
        flushAndClear();

        List<RankContent> result = contentRepository.findAllMainHotContent(10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent().getContentBasicInfo().getName()).isEqualTo("operated");
    }
}
