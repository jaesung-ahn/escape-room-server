package com.wiiee.server.api.infrastructure.repository.content.review;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wiiee.server.api.application.content.review.ReviewGetRequestDTO;
import com.wiiee.server.api.application.review.ReviewStatInfo;
import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.common.State;
import com.wiiee.server.common.domain.content.review.Review;
import com.wiiee.server.common.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;

import static com.wiiee.server.common.domain.company.QCompany.company;
import static com.wiiee.server.common.domain.content.QContent.content;
import static com.wiiee.server.common.domain.content.review.QReview.review;

@RequiredArgsConstructor
@Repository
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager em;

    @Override
    public Page<Review> findAllByReviewGetRequestDTO(User user, Long contentId, ReviewGetRequestDTO dto) {
        final Pageable pageable = dto.toPageable();
        final var reviewList = jpaQueryFactory.select(review)
                .from(review)
                .leftJoin(review.content, content)
                .leftJoin(content.company, company)
                .where(
                        userEq(user),
                        contentIdEq(contentId),
                        stateEq(dto.getStateCode()),
                        cityEq(dto.getCityCode()),
                        review.isApproval.eq(true)
                )
                .orderBy(review.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(reviewList.getResults(), pageable, reviewList.getTotal());
    }

    @Override
    public ReviewStatInfo findReviewAvgAndCountByContent(Long contentId) {
        final var reviewResult = jpaQueryFactory.select(
                    review.count(),
                    review.rating.avg()
                )
                .from(review)
                .where(
                        contentIdEq(contentId),
                        review.isApproval.eq(true)
                )
                .fetchOne();

        return new ReviewStatInfo(reviewResult.get(review.rating.avg()),
                reviewResult.get(review.count()));
    }

    private BooleanExpression contentIdEq(Long contentId) {
        return contentId != null ? review.content.id.eq(contentId) : null;
    }

    private BooleanExpression stateEq(Integer stateCode) {
        return stateCode != null ? content.company.basicInfo.state.eq(State.valueOf(stateCode)) : null;
    }

    private BooleanExpression cityEq(Integer cityCode) {
        return cityCode != null ? content.company.basicInfo.city.eq(City.valueOf(cityCode)) : null;
    }

    private BooleanExpression userEq(User user) {
        return user != null ? review.writer.eq(user) : null;
    }
}