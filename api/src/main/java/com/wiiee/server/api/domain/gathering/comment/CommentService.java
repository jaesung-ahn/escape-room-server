package com.wiiee.server.api.domain.gathering.comment;

import com.wiiee.server.api.application.common.PageRequestDTO;
import com.wiiee.server.api.application.exception.ForbiddenException;
import com.wiiee.server.api.application.exception.ResourceNotFoundException;
import com.wiiee.server.api.application.gathering.comment.CommentModel;
import com.wiiee.server.api.application.gathering.comment.CommentPostRequestDTO;
import com.wiiee.server.api.application.gathering.comment.CommentPutRequestDTO;
import com.wiiee.server.api.application.gathering.comment.MultipleCommentModel;
import com.wiiee.server.api.application.user.UserProfileResponseDTO;
import com.wiiee.server.api.domain.code.GatheringCommentErrorCode;
import com.wiiee.server.api.domain.image.ImageService;
import com.wiiee.server.api.domain.user.UserService;
import com.wiiee.server.common.domain.common.Image;
import com.wiiee.server.common.domain.gathering.comment.Comment;
import com.wiiee.server.common.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final ImageService imageService;
    private final com.wiiee.server.api.domain.gathering.GatheringRepository gatheringRepository;

    @Transactional
    public CommentModel createComment(Long gatheringId, Long userId, CommentPostRequestDTO dto) {
        // Gathering 존재 여부 확인
        gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new ResourceNotFoundException(GatheringCommentErrorCode.ERROR_GATHERING_NOT_FOUND));

        User writer = userService.findById(userId);
        UserProfileResponseDTO userResponse = getUserResponse(writer);
        if (dto.getParentCommentId() != null) {
            Comment parent = findById(dto.getParentCommentId());
            Comment savedComment = commentRepository.save(new Comment(gatheringId, writer, dto.getMessage(), parent));
            parent.addChildren(savedComment);
            CommentModel commentModel = new CommentModel(savedComment, userResponse);
            commentModel.changeIsOwner(userId);
            return commentModel;
        }
        CommentModel commentModel = new CommentModel(commentRepository.save(new Comment(gatheringId, writer, dto.getMessage())), userResponse);
        commentModel.changeIsOwner(userId);
        return commentModel;
    }

    @Transactional(readOnly = true)
    public MultipleCommentModel getComments(Long gatheringId, Long userId) {
        List<Comment> comments = commentRepository.findAllWithChildrenByGatheringId(gatheringId);

        // 모든 댓글(부모 + 자식)의 작성자 프로필 이미지 ID 수집
        List<Long> imageIds = new ArrayList<>();
        for (Comment comment : comments) {
            Long profileImageId = comment.getWriter().getProfile().getProfileImageId();
            if (profileImageId != null) {
                imageIds.add(profileImageId);
            }
            // 자식 댓글의 프로필 이미지 ID도 수집
            for (Comment child : comment.getChildren()) {
                Long childProfileImageId = child.getWriter().getProfile().getProfileImageId();
                if (childProfileImageId != null) {
                    imageIds.add(childProfileImageId);
                }
            }
        }

        // 배치로 이미지 조회 및 Map 변환
        java.util.Map<Long, Image> imageMap = imageIds.isEmpty()
            ? java.util.Collections.emptyMap()
            : imageService.findByIdsIn(imageIds).stream()
                .collect(Collectors.toMap(Image::getId, img -> img, (a, b) -> a, java.util.HashMap::new));

        List<CommentModel> commentModels = comments.stream().map(comment -> {
            CommentModel commentModel = new CommentModel(comment, getUserResponseFromMap(comment.getWriter(), imageMap));
            commentModel.changeIsOwner(userId);

            if (comment.getParent() == null) {
                List<CommentModel> children = getChildCommentModels(userId, comment, imageMap);
                children.sort(Comparator.comparing(CommentModel::getCreateAt));
                commentModel.addAllChildren(children);
            }
            return commentModel;
        }).collect(Collectors.toList());
        return MultipleCommentModel.of(commentModels);
    }

    @Transactional(readOnly = true)
    public MultipleCommentModel getCommentsWithPagination(Long gatheringId, Long userId, PageRequestDTO dto) {
        return null;
    }

    @Transactional
    public CommentModel update(Long commentId, Long userId, CommentPutRequestDTO dto) {
        Comment comment = findById(commentId);
        if (!comment.getWriter().getId().equals(userId)) {
            throw new ForbiddenException(GatheringCommentErrorCode.ERROR_PERMISSION_NOT_ALLOWED);
        }
        comment.changeMessage(dto.getMessage());

        // 댓글 작성자 및 자식 댓글 작성자의 프로필 이미지 ID 수집
        List<Long> imageIds = new ArrayList<>();
        Long profileImageId = comment.getWriter().getProfile().getProfileImageId();
        if (profileImageId != null) {
            imageIds.add(profileImageId);
        }
        for (Comment child : comment.getChildren()) {
            Long childProfileImageId = child.getWriter().getProfile().getProfileImageId();
            if (childProfileImageId != null) {
                imageIds.add(childProfileImageId);
            }
        }

        // 배치로 이미지 조회 및 Map 변환
        java.util.Map<Long, Image> imageMap = imageIds.isEmpty()
            ? java.util.Collections.emptyMap()
            : imageService.findByIdsIn(imageIds).stream()
                .collect(Collectors.toMap(Image::getId, img -> img, (a, b) -> a, java.util.HashMap::new));

        CommentModel commentModel = new CommentModel(comment, getUserResponseFromMap(comment.getWriter(), imageMap));
        commentModel.changeIsOwner(userId);
        List<CommentModel> children = getChildCommentModels(userId, comment, imageMap);
        commentModel.addAllChildren(children);
        return commentModel;
    }

    @Transactional
    public void delete(Long commentId, Long userId) {

        final var comment = findById(commentId);
        if (!comment.getWriter().getId().equals(userId)) {
            throw new ForbiddenException(GatheringCommentErrorCode.ERROR_PERMISSION_NOT_ALLOWED);
        }
        comment.delete();
    }

    private List<CommentModel> getChildCommentModels(Long userId, Comment comment) {
        return comment.getChildren().stream().map(child -> {
            CommentModel childModel = new CommentModel(child, getUserResponse(child.getWriter()));
            childModel.changeIsOwner(userId);
            return childModel;
        }).collect(Collectors.toList());
    }

    private List<CommentModel> getChildCommentModels(Long userId, Comment comment, java.util.Map<Long, Image> imageMap) {
        return comment.getChildren().stream().map(child -> {
            CommentModel childModel = new CommentModel(child, getUserResponseFromMap(child.getWriter(), imageMap));
            childModel.changeIsOwner(userId);
            return childModel;
        }).collect(Collectors.toList());
    }

    private Comment findById(Long id) {
        return commentRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    private UserProfileResponseDTO getUserResponse(User writer) {
        Image userImage = imageService.getImageById(writer.getProfile().getProfileImageId());
        return UserProfileResponseDTO.from(writer, userImage);
    }

    private UserProfileResponseDTO getUserResponseFromMap(User writer, java.util.Map<Long, Image> imageMap) {
        Long profileImageId = writer.getProfile().getProfileImageId();
        Image userImage = profileImageId != null ? imageMap.get(profileImageId) : null;
        return UserProfileResponseDTO.from(writer, userImage);
    }

    @Transactional
    public void deleteAllByGatheringId(Long gatheringId) {
        commentRepository.deleteByGatheringId(gatheringId);
    }
}
