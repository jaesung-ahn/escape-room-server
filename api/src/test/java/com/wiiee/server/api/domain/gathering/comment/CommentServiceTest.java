package com.wiiee.server.api.domain.gathering.comment;

import com.wiiee.server.api.application.exception.ForbiddenException;
import com.wiiee.server.api.application.exception.ResourceNotFoundException;
import com.wiiee.server.api.application.gathering.comment.CommentModel;
import com.wiiee.server.api.application.gathering.comment.CommentPostRequestDTO;
import com.wiiee.server.api.application.gathering.comment.CommentPutRequestDTO;
import com.wiiee.server.api.domain.gathering.GatheringRepository;
import com.wiiee.server.api.domain.image.ImageService;
import com.wiiee.server.api.domain.user.UserService;
import com.wiiee.server.common.domain.common.Image;
import com.wiiee.server.common.domain.gathering.Gathering;
import com.wiiee.server.common.domain.gathering.comment.Comment;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentService 단위 테스트")
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserService userService;
    @Mock
    private ImageService imageService;
    @Mock
    private GatheringRepository gatheringRepository;

    @InjectMocks
    private CommentService commentService;

    private User writer;
    private User otherUser;

    @BeforeEach
    void setUp() {
        writer = new FixtureUser("writer@test.com", "작성자");
        otherUser = new FixtureUser("other@test.com", "다른사용자");
        ReflectionTestUtils.setField(otherUser, "id", 2L);
    }

    private Comment createComment(Long id, Long gatheringId, User commentWriter, String message) {
        Comment comment = new Comment(gatheringId, commentWriter, message);
        ReflectionTestUtils.setField(comment, "id", id);
        ReflectionTestUtils.setField(comment, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(comment, "children", new ArrayList<>());
        return comment;
    }

    @Nested
    @DisplayName("createComment")
    class CreateComment {

        @Test
        @DisplayName("존재하지 않는 동행모집에 댓글 작성하면 ResourceNotFoundException 발생")
        void throwsNotFoundWhenGatheringNotExists() {
            // given
            given(gatheringRepository.findById(999L)).willReturn(Optional.empty());

            CommentPostRequestDTO dto = new CommentPostRequestDTO("댓글입니다");

            // when & then
            assertThatThrownBy(() -> commentService.createComment(999L, 1L, dto))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("대댓글이 정상적으로 생성된다")
        void createChildCommentSuccessfully() {
            // given
            Gathering gathering = mock(Gathering.class);
            given(gatheringRepository.findById(10L)).willReturn(Optional.of(gathering));
            given(userService.findById(1L)).willReturn(writer);
            given(imageService.getImageById(any())).willReturn(new Image(""));

            Comment parent = createComment(1L, 10L, writer, "부모 댓글");

            given(commentRepository.findById(1L)).willReturn(Optional.of(parent));

            Comment savedChild = new Comment(10L, writer, "자식 댓글", parent);
            ReflectionTestUtils.setField(savedChild, "id", 2L);
            ReflectionTestUtils.setField(savedChild, "createdAt", LocalDateTime.now());
            ReflectionTestUtils.setField(savedChild, "children", new ArrayList<>());
            given(commentRepository.save(any(Comment.class))).willReturn(savedChild);

            CommentPostRequestDTO dto = new CommentPostRequestDTO(1L, "자식 댓글");

            // when
            CommentModel result = commentService.createComment(10L, 1L, dto);

            // then
            assertThat(result).isNotNull();
            verify(commentRepository).save(any(Comment.class));
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("작성자가 아닌 사용자가 수정하면 ForbiddenException 발생")
        void throwsForbiddenWhenNotWriter() {
            // given
            Comment comment = createComment(1L, 10L, writer, "원래 댓글");
            given(commentRepository.findById(1L)).willReturn(Optional.of(comment));

            CommentPutRequestDTO dto = new CommentPutRequestDTO("수정 댓글");

            // when & then
            assertThatThrownBy(() -> commentService.update(1L, 2L, dto))
                    .isInstanceOf(ForbiddenException.class);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("작성자가 아닌 사용자가 삭제하면 ForbiddenException 발생")
        void throwsForbiddenWhenNotWriter() {
            // given
            Comment comment = createComment(1L, 10L, writer, "댓글");
            given(commentRepository.findById(1L)).willReturn(Optional.of(comment));

            // when & then
            assertThatThrownBy(() -> commentService.delete(1L, 2L))
                    .isInstanceOf(ForbiddenException.class);
        }

        @Test
        @DisplayName("작성자가 정상적으로 댓글을 삭제한다")
        void deleteSuccessfully() {
            // given
            Comment comment = createComment(1L, 10L, writer, "댓글");
            given(commentRepository.findById(1L)).willReturn(Optional.of(comment));

            // when
            commentService.delete(1L, 1L);

            // then
            assertThat(comment.getDeleted()).isTrue();
        }
    }
}
