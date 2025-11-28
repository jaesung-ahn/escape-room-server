package com.wiiee.server.api.infrastructure.repository.gathering;

import com.amazonaws.util.CollectionUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wiiee.server.api.application.gathering.GatheringGetRequestDTO;
import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.common.State;
import com.wiiee.server.common.domain.content.Difficulty;
import com.wiiee.server.common.domain.content.Genre;
import com.wiiee.server.common.domain.gathering.Gathering;
import com.wiiee.server.common.domain.gathering.member.GatheringMember;
import com.wiiee.server.common.domain.gathering.member.Status;
import com.wiiee.server.common.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.wiiee.server.common.domain.company.QCompany.company;
import static com.wiiee.server.common.domain.content.QContent.content;
import static com.wiiee.server.common.domain.gathering.QGathering.gathering;
import static com.wiiee.server.common.domain.gathering.member.QGatheringMember.gatheringMember;
import static io.jsonwebtoken.lang.Strings.hasText;

@RequiredArgsConstructor
@Repository
public class GatheringCustomRepositoryImpl implements GatheringCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Gathering> findAllByGatheringGetRequestDTO(GatheringGetRequestDTO dto, Pageable pageable) {
        List<Difficulty> difficultyList = dto.getDifficultyCodes() != null ? dto.getDifficultyCodes().stream().map(
                difficulty -> Difficulty.valueOf(difficulty)
                )
                .collect(Collectors.toList()) : null;
        final var list = jpaQueryFactory.select(gathering)
                .from(gathering)
                .join(gathering.content, content)
                .join(gathering.content.company, company)
                .where(
                        gathering.deleted.eq(false),
                        titleContains(dto.getTitle()),
                        difficultyContains(difficultyList),
//                        stateEq(dto.getStateCode()),
//                        cityEq(dto.getCityCode()),
                        genreEq(dto.getGenreCode())
                )
                .orderBy()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        return new PageImpl<>(list.getResults(), pageable, list.getTotal());
    }

    @Override
    public int findCountByUser(User user) {
        return jpaQueryFactory
                .selectFrom(gatheringMember)
                .where(
                        gatheringMember.status.eq(Status.APPROVAL),
                        gatheringMember.user.eq(user)
                ).fetch().size();
    }

    private BooleanExpression titleContains(String title) {
        return hasText(title) ? gathering.gatheringInfo.title.contains(title) : null;
    }

    private BooleanExpression difficultyContains(List<Difficulty> difficultyCodes) {
        return difficultyCodes != null ? gathering.content.contentBasicInfo.difficulty.in(difficultyCodes) : null;
    }

    private BooleanExpression stateEq(Integer stateCode) {
        return stateCode != null ? gathering.content.company.basicInfo.state.eq(State.valueOf(stateCode)) : null;
    }

    private BooleanExpression cityEq(Integer cityCode) {
        return cityCode != null ? gathering.content.company.basicInfo.city.eq(City.valueOf(cityCode)) : null;
    }

    private BooleanExpression genreEq(Integer genreCode) {
        return genreCode != null ? content.contentBasicInfo.genre.eq(Genre.valueOf(genreCode)) : null;
    }

    private BooleanExpression genreIn(List<Integer> genreCodes){
        return !CollectionUtils.isNullOrEmpty(genreCodes) ?
                gathering.content.contentBasicInfo.genre.in(genreCodes.stream().map(Genre::valueOf).collect(Collectors.toList())) : null;
    }

    @Override
    public List<Gathering> findAllMyGathering(User user) {
        return jpaQueryFactory.select(gathering)
                .from(gathering)
                .join(gathering.gatheringMembers, gatheringMember)
                .on(gatheringMember.user.eq(user))
                .where(gathering.deleted.eq(false))
                .fetch();
    }

    @Override
    public GatheringMember findGatheringMember(Gathering gathering, User user) {
        return jpaQueryFactory.selectFrom(gatheringMember)
                .where(gatheringMember.gathering.eq(gathering),
                        gatheringMember.user.eq(user))
                .fetchOne();
    }

}