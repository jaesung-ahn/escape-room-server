package com.wiiee.server.api.domain.content.review;

import com.wiiee.server.api.application.content.review.ReviewModel;
import com.wiiee.server.api.application.content.review.ReviewPostRequestDTO;
import com.wiiee.server.api.application.content.review.ReviewPutRequestDTO;
import com.wiiee.server.api.application.exception.ForbiddenException;
import com.wiiee.server.api.domain.content.ContentService;
import com.wiiee.server.api.domain.image.ImageService;
import com.wiiee.server.api.domain.slack.SlackService;
import com.wiiee.server.api.domain.user.UserService;
import com.wiiee.server.common.domain.content.Content;
import com.wiiee.server.common.domain.content.ContentBasicInfo;
import com.wiiee.server.common.domain.content.review.Review;
import com.wiiee.server.common.domain.user.FixtureUser;
import com.wiiee.server.common.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewService 단위 테스트")
class ReviewServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private ContentService contentService;
    @Mock
    private ImageService imageService;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private SlackService slackService;

    @InjectMocks
    private ReviewService reviewService;

    private User writer;
    private User otherUser;
    private Content realContent;

    @BeforeEach
    void setUp() {
        writer = new FixtureUser("writer@test.com", "리뷰어");
        otherUser = new FixtureUser("other@test.com", "다른사용자");
        ReflectionTestUtils.setField(otherUser, "id", 2L);

        realContent = new Content(null, new ContentBasicInfo("테스트 놀거리"));
        ReflectionTestUtils.setField(realContent, "id", 1L);
    }

    private Review createReview(String message, User reviewWriter) {
        Review review = new Review(message, reviewWriter, realContent, 4.0, 2, List.of(), LocalDate.now());
        ReflectionTestUtils.setField(review, "id", 100L);
        ReflectionTestUtils.setField(review, "createdAt", LocalDateTime.now());
        return review;
    }

    private ReviewPostRequestDTO createPostDTO() {
        ReviewPostRequestDTO dto = new ReviewPostRequestDTO();
        ReflectionTestUtils.setField(dto, "message", "좋은 리뷰");
        ReflectionTestUtils.setField(dto, "rating", 4.5);
        ReflectionTestUtils.setField(dto, "joinNumber", 3);
        ReflectionTestUtils.setField(dto, "imageIds", List.of());
        ReflectionTestUtils.setField(dto, "realGatherDate", LocalDate.now());
        return dto;
    }

    /**
     * createReview 내부에서 content.addReview()가 새 Review를 생성하는데,
     * JPA context 밖이므로 createdAt이 null이 됨.
     * mock Content를 사용하여 createdAt이 설정된 Review를 반환하도록 구성.
     */
    private Content createMockContentForReviewCreation() {
        Content mockContent = mock(Content.class);
        ContentBasicInfo mockBasicInfo = mock(ContentBasicInfo.class);
        given(mockContent.getContentBasicInfo()).willReturn(mockBasicInfo);
        given(mockBasicInfo.getName()).willReturn("테스트 놀거리");

        Review reviewWithCreatedAt = new Review("좋은 리뷰", writer, mockContent, 4.5, 3, List.of(), LocalDate.now());
        ReflectionTestUtils.setField(reviewWithCreatedAt, "createdAt", LocalDateTime.now());

        given(mockContent.addReview(any(), anyString(), anyDouble(), anyInt(), anyList(), any(LocalDate.class)))
                .willReturn(reviewWithCreatedAt);
        return mockContent;
    }

    @Nested
    @DisplayName("createReview")
    class CreateReview {

        @Test
        @DisplayName("리뷰 생성 시 Slack 메시지가 전송된다")
        void sendsSlackMessageOnCreate() {
            // given
            Content mockContent = createMockContentForReviewCreation();
            given(userService.findById(1L)).willReturn(writer);
            given(contentService.findById(1L)).willReturn(Optional.of(mockContent));
            given(imageService.findByIdsIn(any())).willReturn(List.of());

            // when
            ReviewModel result = reviewService.createReview(1L, 1L, createPostDTO());

            // then
            assertThat(result).isNotNull();
            verify(slackService).sendSlackMessage(anyString(), anyString());
        }

        @Test
        @DisplayName("Slack 전송 실패해도 리뷰는 정상 생성된다")
        void reviewCreatedEvenWhenSlackFails() {
            // given
            Content mockContent = createMockContentForReviewCreation();
            given(userService.findById(1L)).willReturn(writer);
            given(contentService.findById(1L)).willReturn(Optional.of(mockContent));
            given(imageService.findByIdsIn(any())).willReturn(List.of());
            doThrow(new RuntimeException("Slack error")).when(slackService).sendSlackMessage(anyString(), anyString());

            // when
            ReviewModel result = reviewService.createReview(1L, 1L, createPostDTO());

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("updateReview")
    class UpdateReview {

        @Test
        @DisplayName("작성자가 아닌 사용자가 수정하면 ForbiddenException 발생")
        void throwsForbiddenWhenNotAuthor() {
            // given
            Review review = createReview("리뷰 내용", writer);

            given(userService.findById(2L)).willReturn(otherUser);
            given(reviewRepository.findById(100L)).willReturn(Optional.of(review));

            // when & then
            assertThatThrownBy(() -> reviewService.updateReview(2L, 100L,
                    new ReviewPutRequestDTO("수정 내용", 3.0, List.of())))
                    .isInstanceOf(ForbiddenException.class);
        }

        @Test
        @DisplayName("작성자가 정상적으로 리뷰를 수정한다")
        void updateSuccessfully() {
            // given
            Review review = createReview("리뷰 내용", writer);

            given(userService.findById(1L)).willReturn(writer);
            given(reviewRepository.findById(100L)).willReturn(Optional.of(review));
            given(imageService.findByIdsIn(any())).willReturn(List.of());

            // when
            ReviewModel result = reviewService.updateReview(1L, 100L,
                    new ReviewPutRequestDTO("수정된 리뷰", 5.0, List.of()));

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("deleteReview")
    class DeleteReview {

        @Test
        @DisplayName("작성자가 아닌 사용자가 삭제하면 ForbiddenException 발생")
        void throwsForbiddenWhenNotAuthor() {
            // given
            Review review = createReview("리뷰 내용", writer);

            given(userService.findById(2L)).willReturn(otherUser);
            given(reviewRepository.findById(100L)).willReturn(Optional.of(review));

            // when & then
            assertThatThrownBy(() -> reviewService.deleteReview(2L, 100L))
                    .isInstanceOf(ForbiddenException.class);
        }
    }
}
