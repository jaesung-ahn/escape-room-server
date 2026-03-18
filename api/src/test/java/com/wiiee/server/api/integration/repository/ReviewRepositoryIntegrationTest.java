package com.wiiee.server.api.integration.repository;

import com.wiiee.server.api.application.content.review.ReviewGetRequestDTO;
import com.wiiee.server.api.application.review.ReviewStatInfo;
import com.wiiee.server.api.domain.content.review.ReviewRepository;
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
import com.wiiee.server.common.domain.content.review.Review;
import com.wiiee.server.common.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewRepositoryIntegrationTest extends RepositoryIntegrationTest {

    @Autowired
    private ReviewRepository reviewRepository;

    private User writer1;
    private User writer2;
    private Company seoulCompany;
    private Company busanCompany;
    private Content content1;
    private Content content2;

    @BeforeEach
    void setUp() {
        writer1 = tem.persistAndFlush(User.of("writer1@test.com", "writer1"));
        writer2 = tem.persistAndFlush(User.of("writer2@test.com", "writer2"));

        AdminUser adminUser = tem.persistAndFlush(AdminUser.of("admin@test.com", "password"));
        seoulCompany = tem.persistAndFlush(new Company(
                adminUser,
                new CompanyBasicInfo("SeoulCo", State.SEOUL, City.SONGPAGU,
                        "addr", null, null, null, null, true,
                        List.of(1, 2, 3, 4, 5), true, new ArrayList<>()),
                null));
        busanCompany = tem.persistAndFlush(new Company(
                adminUser,
                new CompanyBasicInfo("BusanCo", State.BUSAN, City.BUSANJINGU,
                        "addr2", null, null, null, null, true,
                        List.of(1, 2, 3, 4, 5), true, new ArrayList<>()),
                null));

        content1 = tem.persistAndFlush(new Content(seoulCompany,
                new ContentBasicInfo("Content1", Genre.THRILLER, "info", 60,
                        null, null, false, 2, 4, Difficulty.LEVEL1,
                        new ArrayList<>(), false, false, null, true)));
        content2 = tem.persistAndFlush(new Content(busanCompany,
                new ContentBasicInfo("Content2", Genre.ADVENTURE, "info", 60,
                        null, null, false, 2, 4, Difficulty.LEVEL2,
                        new ArrayList<>(), false, false, null, true)));
    }

    private Review createReview(User writer, Content content, Double rating, boolean approved) {
        Review review = content.addReview(writer, "review message", rating, 3,
                new ArrayList<>(), LocalDate.now());
        if (approved) {
            review.updateApproval(true);
        }
        return tem.persistAndFlush(review);
    }

    @Test
    @DisplayName("승인된 리뷰만 반환한다")
    void findAll_onlyApproved() {
        createReview(writer1, content1, 4.0, true);
        createReview(writer2, content1, 3.0, false);
        flushAndClear();

        ReviewGetRequestDTO dto = ReviewGetRequestDTO.builder().page(1).size(10).build();
        Page<Review> result = reviewRepository.findAllByReviewGetRequestDTO(null, null, dto);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).isApproval()).isTrue();
    }

    @Test
    @DisplayName("놀거리 ID로 필터링한다")
    void findAll_filterByContentId() {
        createReview(writer1, content1, 4.0, true);
        createReview(writer2, content2, 3.5, true);
        flushAndClear();

        ReviewGetRequestDTO dto = ReviewGetRequestDTO.builder().page(1).size(10).build();
        Page<Review> result = reviewRepository.findAllByReviewGetRequestDTO(null, content1.getId(), dto);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getContent().getId()).isEqualTo(content1.getId());
    }

    @Test
    @DisplayName("사용자별로 필터링한다")
    void findAll_filterByUser() {
        createReview(writer1, content1, 4.0, true);
        createReview(writer2, content1, 3.0, true);
        flushAndClear();

        ReviewGetRequestDTO dto = ReviewGetRequestDTO.builder().page(1).size(10).build();
        Page<Review> result = reviewRepository.findAllByReviewGetRequestDTO(writer1, null, dto);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getWriter().getId()).isEqualTo(writer1.getId());
    }

    @Test
    @DisplayName("지역으로 필터링한다")
    void findAll_filterByState() {
        createReview(writer1, content1, 4.0, true);
        createReview(writer2, content2, 3.5, true);
        flushAndClear();

        ReviewGetRequestDTO dto = ReviewGetRequestDTO.builder()
                .page(1).size(10).stateCode(State.SEOUL.getCode()).build();
        Page<Review> result = reviewRepository.findAllByReviewGetRequestDTO(null, null, dto);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getContent().getId()).isEqualTo(content1.getId());
    }

    @Test
    @DisplayName("페이지네이션이 정상 동작한다")
    void findAll_pagination() {
        for (int i = 0; i < 5; i++) {
            User writer = tem.persistAndFlush(User.of("user" + i + "@test.com", "user" + i));
            createReview(writer, content1, 4.0, true);
        }
        flushAndClear();

        ReviewGetRequestDTO dto = ReviewGetRequestDTO.builder().page(1).size(2).build();
        Page<Review> result = reviewRepository.findAllByReviewGetRequestDTO(null, null, dto);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.hasNext()).isTrue();
    }

    @Test
    @DisplayName("평균 평점과 리뷰 수를 정확히 반환한다")
    void findReviewAvgAndCount_returnsCorrectStats() {
        createReview(writer1, content1, 4.0, true);
        User writer3 = tem.persistAndFlush(User.of("writer3@test.com", "writer3"));
        createReview(writer3, content1, 2.0, true);
        createReview(writer2, content1, 5.0, false);
        flushAndClear();

        ReviewStatInfo stat = reviewRepository.findReviewAvgAndCountByContent(content1.getId());

        assertThat(stat.getReviewCnt()).isEqualTo(2);
        assertThat(stat.getRatingAvg()).isEqualTo(3.0);
    }

    @Test
    @DisplayName("리뷰가 없는 놀거리는 0을 반환한다")
    void findReviewAvgAndCount_noReviews_returnsZero() {
        flushAndClear();

        ReviewStatInfo stat = reviewRepository.findReviewAvgAndCountByContent(content1.getId());

        assertThat(stat.getReviewCnt()).isEqualTo(0);
    }
}
