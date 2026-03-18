package com.wiiee.server.api.integration.service;

import com.wiiee.server.api.application.content.ContentPostRequestDTO;
import com.wiiee.server.api.application.content.ContentSimpleModel;
import com.wiiee.server.api.domain.content.ContentRepository;
import com.wiiee.server.api.domain.content.ContentService;
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
import com.wiiee.server.common.domain.content.RankContent;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ContentServiceCacheIntegrationTest extends ServiceIntegrationTest {

    @Autowired
    private ContentService contentService;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private EntityManager entityManager;

    private Long companyId;

    @BeforeEach
    void setUpData() {
        transactionTemplate.executeWithoutResult(status -> {
            AdminUser adminUser = entityManager.merge(AdminUser.of("admin@test.com", "password"));
            Company company = entityManager.merge(new Company(
                    adminUser,
                    new CompanyBasicInfo("TestCo", State.SEOUL, City.SONGPAGU,
                            "addr", null, null, null, null, true,
                            List.of(1, 2, 3, 4, 5), true, new ArrayList<>()),
                    null));

            Content content = entityManager.merge(new Content(company,
                    new ContentBasicInfo("HotContent", Genre.THRILLER, "info", 60,
                            null, null, false, 2, 4, Difficulty.LEVEL1,
                            new ArrayList<>(), false, false, null, true)));
            entityManager.merge(new RankContent(1, content));
            entityManager.flush();
            companyId = company.getId();
        });
    }

    @Test
    @DisplayName("첫 호출 시 DB에서 인기 놀거리를 조회한다")
    void getMainHotContentList_returnsFromDb() {
        List<ContentSimpleModel> result = contentService.getMainHotContentList();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContentName()).isEqualTo("HotContent");
    }

    @Test
    @DisplayName("두 번째 호출 시 동일한 결과를 반환한다")
    void getMainHotContentList_secondCall_returnsSameResult() {
        List<ContentSimpleModel> first = contentService.getMainHotContentList();
        List<ContentSimpleModel> second = contentService.getMainHotContentList();

        assertThat(first).isNotEmpty();
        assertThat(second).isNotEmpty();
        assertThat(first.get(0).getContentName()).isEqualTo(second.get(0).getContentName());
    }

    @Test
    @DisplayName("놀거리 생성 후 인기 놀거리 목록을 새로 조회할 수 있다")
    void createContent_thenGetHotList_works() {
        List<ContentSimpleModel> before = contentService.getMainHotContentList();
        assertThat(before).hasSize(1);

        ContentPostRequestDTO dto = new ContentPostRequestDTO(
                companyId, "NewContent", Genre.ADVENTURE.getCode(),
                new ArrayList<>(), 2, 4, "new info", 60,
                0, 5, false, Difficulty.LEVEL2.getCode(),
                false, false, null, true, new ArrayList<>());
        contentService.createNewContent(dto);

        List<ContentSimpleModel> after = contentService.getMainHotContentList();
        assertThat(after).isNotEmpty();
    }
}
