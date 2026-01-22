package com.wiiee.server.common.domain.content.review;

import com.wiiee.server.common.domain.BaseEntity;
import com.wiiee.server.common.domain.content.Content;
import com.wiiee.server.common.domain.user.User;
import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review", indexes = {
    @Index(name = "idx_review_content_approval", columnList = "content_id, is_approval")
})
@Entity
public class Review extends BaseEntity {

    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "user_id",
            referencedColumnName = "user_id",
            nullable = false)
    private User writer;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "content_id",
            referencedColumnName = "content_id",
            nullable = false)
    private Content content;

    private Double rating;

    // 참여자 수(동행모집의 참여자 수가 아니라 개인이 따로 입력하는 참여자 수)
    private Integer joinNumber;

    @Type(ListArrayType.class)
    @Column(columnDefinition = "bigint[]")
    private List<Long> imageIds = new ArrayList<>();

    // 실제 동행 일자 - 필수
    @Column(nullable = false)
    private LocalDate realGatherDate;

    // 승인 여부
    @Column(name = "is_approval", columnDefinition = "boolean default false")
    private boolean isApproval = false;

    public Review(String message, User writer, Content content, Double rating, Integer joinNumber, List<Long> imageIds, LocalDate realGatherDate) {
        this.message = message;
        this.writer = writer;
        this.content = content;
        this.rating = rating;
        this.joinNumber = joinNumber;
        this.imageIds = imageIds;
        this.realGatherDate = realGatherDate;
    }

    public void updateReview(String message, Double rating, List<Long> imageIds){
        this.message = message;
        this.rating = rating;
        this.imageIds = imageIds;
    }

    public void updateApproval(boolean isApproval) {
        this.isApproval = isApproval;
    }

    /**
     * 대표 이미지 ID를 반환합니다.
     * imageIds가 비어있거나 null인 경우 null을 반환하여
     * 호출자가 안전하게 처리할 수 있도록 합니다.
     *
     * @return 첫 번째 이미지 ID, 없으면 null
     */
    public Long getRepresentativeImageId() {
        if (imageIds == null || imageIds.isEmpty()) {
            return null;
        }
        return imageIds.get(0);
    }
}
