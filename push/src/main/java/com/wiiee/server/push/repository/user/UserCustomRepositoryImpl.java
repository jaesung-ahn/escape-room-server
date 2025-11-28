package com.wiiee.server.push.repository.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wiiee.server.common.domain.user.User;
import com.wiiee.server.common.domain.user.UserOS;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.wiiee.server.common.domain.user.QUser.user;

@RequiredArgsConstructor
@Repository
public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<User> findAllForEvent() {

        final List<User> resultList = jpaQueryFactory.select(user)
                .from(user)
                .where(user.profile.isPushEvent.eq(true),
                        user.profile.userOs.eq(UserOS.AOS)
                )
                .fetch();

        return resultList;
    }

//    private BooleanExpression eqCompanyId(Long companyId) {
//        if (companyId == null || companyId == 0){
//            return null;
//        }
//        return content.company.id.eq(companyId);
//    }
}
