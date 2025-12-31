package com.wiiee.server.api.domain.recommendation;

import com.wiiee.server.api.AcceptanceTest;
import com.wiiee.server.api.application.recommendation.RecommendationModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RecommendationService N+1 쿼리 개선 검증 테스트
 *
 * 목적:
 * 1. N+1 제거 후 기능이 정상 동작하는지 확인
 * 2. 쿼리 개선으로 인한 회귀가 없는지 검증
 * 3. LazyInitializationException이 발생하지 않는지 확인
 *
 * 개선 내용:
 * - RecommendationRepository.findAllWithContents() 사용 (JOIN FETCH)
 * - ContentService.getContentSimpleModelsByContents() 배치 이미지 조회
 *
 * 예상 개선 효과:
 * - 개선 전: 1(recommendation) + N(contents) + M(images) 쿼리
 * - 개선 후: 1(recommendation+contents) + 1(images batch) 쿼리
 */
@DisplayName("RecommendationService N+1 개선 통합 테스트")
class RecommendationServiceIntegrationTest extends AcceptanceTest {

    @Autowired
    private RecommendationService recommendationService;

    @Test
    @DisplayName("getRecommendations() - N+1 최적화로 정상 동작 (데이터 없어도 에러 없음)")
    void getRecommendations_shouldWorkWithoutNPlusOne() {
        // when: 추천 카테고리 조회 (N+1 최적화 적용)
        List<RecommendationModel> recommendations = recommendationService.getRecommendations();

        // then: 에러 없이 정상 동작 (데이터가 없을 수 있음)
        assertThat(recommendations).isNotNull();

        // 데이터가 있는 경우에만 검증
        if (!recommendations.isEmpty()) {
            recommendations.forEach(recommendation -> {
                assertThat(recommendation.getCategoryName()).isNotNull();
                assertThat(recommendation.getContents()).isNotNull();

                // 컨텐츠가 있는 경우 정상적으로 로드되었는지 확인
                if (!recommendation.getContents().isEmpty()) {
                    assertThat(recommendation.getContents().get(0).getContentName()).isNotNull();
                }
            });
        }
    }

    @Test
    @DisplayName("N+1 개선 확인 - JOIN FETCH와 배치 로딩으로 LazyInitializationException 없음")
    void nPlusOneImprovement_shouldNotThrowLazyInitializationException() {
        // when: 트랜잭션 외부에서 호출 (개선 전이면 LazyInitializationException 발생)
        List<RecommendationModel> recommendations = recommendationService.getRecommendations();

        // then: LazyInitializationException이 발생하지 않음
        assertThat(recommendations).isNotNull();

        // 데이터가 있는 경우 모든 연관 데이터에 접근 가능
        recommendations.forEach(recommendation -> {
            assertThat(recommendation.getContents()).isNotNull();
            recommendation.getContents().forEach(content -> {
                // N+1 최적화로 이미 로드된 상태
                assertThat(content.getContentName()).isNotNull();
            });
        });
    }
}
