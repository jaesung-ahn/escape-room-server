package com.wiiee.server.api.infrastructure.repository.content;

import com.amazonaws.util.CollectionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wiiee.server.api.application.content.ContentGetRequestDTO;
import com.wiiee.server.api.application.content.ContentOrderType;
import com.wiiee.server.api.application.content.ContentResponseDTO;
import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.common.State;
import com.wiiee.server.common.domain.content.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.wiiee.server.common.domain.company.QCompany.company;
import static com.wiiee.server.common.domain.content.QContent.content;
import static com.wiiee.server.common.domain.content.QRankContent.rankContent;
import static com.wiiee.server.common.domain.content.review.QReview.review;

@RequiredArgsConstructor
@Repository
public class ContentCustomRepositoryImpl implements ContentCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Content> findAllByContentGetRequestDTO(ContentGetRequestDTO dto, Pageable pageable) {
        final var fetchList = jpaQueryFactory.select(content)
                .from(content)
                .join(content.company, company).fetchJoin()
                .leftJoin(content.reviews, review).on(
                        review.isNotNull(), review.isApproval.eq(true)
                )
                .where(
                        companyIdEq(dto.getCompanyId()),
                        nameContains(dto.getName()),
                        stateEq(dto.getStateCode()),
                        cityEq(dto.getCityCode()),
                        genreIn(dto.getGenreCodes()),
                        difficultyEq(dto.getDifficultyCode()),
                        escapeTypeEq(dto.getEscapeTypeCode()),
                        createAtGoe(dto.getFrom()),
                        contentIdNe(dto.getExceptContentId()),
                        content.contentBasicInfo.isOperated.eq(true)
                )
                .groupBy(content, company)
                .orderBy(orderSpecifier(dto.getContentOrderType()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 count
        long contentCnt = jpaQueryFactory.select(content)
                .from(content)
                .where(
                        companyIdEq(dto.getCompanyId()),
                        nameContains(dto.getName()),
                        stateEq(dto.getStateCode()),
                        cityEq(dto.getCityCode()),
                        genreIn(dto.getGenreCodes()),
                        difficultyEq(dto.getDifficultyCode()),
                        escapeTypeEq(dto.getEscapeTypeCode()),
                        createAtGoe(dto.getFrom()),
                        contentIdNe(dto.getExceptContentId()),
                        content.contentBasicInfo.isOperated.eq(true)
                )
                .fetchCount();

        return new PageImpl<>(fetchList, pageable, contentCnt);
    }

    @Override
    public Page<ContentResponseDTO> findContentModelByContentGetRequestDTO(ContentGetRequestDTO dto, Pageable pageable) {
        final var list = jpaQueryFactory.select(
                        Projections.constructor(ContentResponseDTO.class,
                                content.id,
                                content.contentBasicInfo.imageIds,
                                content.contentBasicInfo.isCaution,
                                content.company.basicInfo.name,
                                content.reviews.any().rating.avg(),
                                content.reviews.size(),
                                content.contentBasicInfo.name
                        )
                )
                .from(content)
                .leftJoin(content.company, company)
                .leftJoin(content.reviews, review)
                .where(
                        companyIdEq(dto.getCompanyId()),
                        nameContains(dto.getName()),
                        stateEq(dto.getStateCode()),
                        cityEq(dto.getCityCode()),
                        genreIn(dto.getGenreCodes()),
                        difficultyEq(dto.getDifficultyCode()),
                        escapeTypeEq(dto.getEscapeTypeCode()),
                        createAtGoe(dto.getFrom()),
                        contentIdNe(dto.getExceptContentId())
                )
                .groupBy(content.id)
                .orderBy(orderSpecifier(dto.getContentOrderType()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(list.getResults(), pageable, list.getTotal());
    }

    @Override
    public List<RankContent> findAllMainHotContent(int limit) {
        return jpaQueryFactory.selectFrom(rankContent)
                .join(rankContent.content, content).fetchJoin()
                .join(content.company, company).fetchJoin()
                .where(
                        rankContent.content.contentBasicInfo.isOperated.eq(true)
                )
                .orderBy(new OrderSpecifier<>(Order.ASC, rankContent.rank).nullsLast())
                .limit(limit)
                .fetch();
    }

    private BooleanExpression companyIdEq(Long companyId) {
        return companyId != null ? content.company.id.eq(companyId) : null;
    }

    private BooleanExpression nameContains(String name) {
        return StringUtils.hasText(name) ? content.contentBasicInfo.name.contains(name) : null;
    }

    private BooleanExpression stateEq(Integer stateCode) {
        return stateCode != null ? content.company.basicInfo.state.eq(State.valueOf(stateCode)) : null;
    }

    private BooleanExpression cityEq(Integer cityCode) {
        return cityCode != null ? content.company.basicInfo.city.eq(City.valueOf(cityCode)) : null;
    }

    private BooleanExpression genreEq(Integer genreCode) {
        return genreCode != null ? content.contentBasicInfo.genre.eq(Genre.valueOf(genreCode)) : null;
    }

    private BooleanExpression genreIn(List<Integer> genreCodes){
        return !CollectionUtils.isNullOrEmpty(genreCodes) ?
                content.contentBasicInfo.genre.in(genreCodes.stream().map(Genre::valueOf).collect(Collectors.toList())) : null;
    }

    private BooleanExpression difficultyEq(Integer difficultyCode) {
        return difficultyCode != null ? content.contentBasicInfo.difficulty.eq(Difficulty.valueOf(difficultyCode)) : null;
    }

    private BooleanExpression escapeTypeEq(Integer escapeTypeCode) {
        return escapeTypeCode != null ? content.contentBasicInfo.escapeType.eq(EscapeType.valueOf(escapeTypeCode)) : null;
    }

    private BooleanExpression createAtGoe(LocalDateTime from) {
        return from != null ? content.createdAt.goe(from) : null;
    }

    private BooleanExpression contentIdNe(Long contentId) {
        return contentId != null ? content.id.ne(contentId) : null;
    }

    private OrderSpecifier<?> orderSpecifier(ContentOrderType orderType) {
        if(orderType != null) {
            if(orderType.equals(ContentOrderType.RATING)) {
                return new OrderSpecifier<>(Order.DESC, content.reviews.any().rating.avg()).nullsLast();
            } else if(orderType.equals(ContentOrderType.LATEST)) {
                return new OrderSpecifier<>(Order.DESC, content.createdAt).nullsLast();
            }
        }
        return new OrderSpecifier<>(Order.DESC, content.createdAt).nullsLast();
    }

}