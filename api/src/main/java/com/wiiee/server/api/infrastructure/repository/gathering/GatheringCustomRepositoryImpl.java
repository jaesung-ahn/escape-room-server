package com.wiiee.server.api.infrastructure.repository.gathering;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wiiee.server.api.application.gathering.GatheringGetRequestDTO;
import com.wiiee.server.common.domain.content.Difficulty;
import com.wiiee.server.common.domain.content.Genre;
import com.wiiee.server.common.domain.gathering.Gathering;
import com.wiiee.server.common.domain.gathering.member.GatheringMember;
import com.wiiee.server.common.domain.gathering.member.Status;
import com.wiiee.server.common.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
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
        List<Difficulty> difficultyList = dto.getDifficultyCodes() != null
                ? dto.getDifficultyCodes().stream().map(Difficulty::valueOf).collect(Collectors.toList())
                : null;

        BooleanExpression[] conditions = {
                gathering.deleted.eq(false),
                titleContains(dto.getTitle()),
                difficultyContains(difficultyList),
                genreEq(dto.getGenreCode())
        };

        List<Gathering> results = jpaQueryFactory.select(gathering)
                .from(gathering)
                .join(gathering.content, content).fetchJoin()
                .join(content.company, company).fetchJoin()
                .where(conditions)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        var countQuery = jpaQueryFactory.select(gathering.count())
                .from(gathering)
                .join(gathering.content, content)
                .where(conditions);

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }

    @Override
    public int findCountByUser(User user) {
        return jpaQueryFactory.select(gatheringMember.count())
                .from(gatheringMember)
                .where(
                        gatheringMember.status.eq(Status.APPROVAL),
                        gatheringMember.user.eq(user)
                )
                .fetchOne()
                .intValue();
    }

    private BooleanExpression titleContains(String title) {
        return hasText(title) ? gathering.gatheringInfo.title.contains(title) : null;
    }

    private BooleanExpression difficultyContains(List<Difficulty> difficultyCodes) {
        return difficultyCodes != null ? gathering.content.contentBasicInfo.difficulty.in(difficultyCodes) : null;
    }

    private BooleanExpression genreEq(Integer genreCode) {
        return genreCode != null ? content.contentBasicInfo.genre.eq(Genre.valueOf(genreCode)) : null;
    }

    @Override
    public List<Gathering> findAllMyGathering(User user) {
        return jpaQueryFactory.select(gathering)
                .from(gathering)
                .join(gathering.content, content).fetchJoin()
                .join(content.company, company).fetchJoin()
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
