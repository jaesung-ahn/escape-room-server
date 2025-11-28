package com.wiiee.server.admin.repository.user;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wiiee.server.common.domain.user.User;
import com.wiiee.server.common.domain.user.UserOS;
import com.wiiee.server.common.domain.user.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import static com.wiiee.server.common.domain.user.QUser.user;

@RequiredArgsConstructor
@Repository
public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<User> findAllByEnablePush(boolean eventCond) {
        return jpaQueryFactory
                .selectFrom(user)
                .where(isPushEventEq(eventCond), isEnabled(), isAndroid())
                .fetch();
    }

    private Predicate isAndroid() {
        return user.profile.userOs.eq(UserOS.AOS);
    }

    private Predicate isEnabled() {
        return user.profile.userStatus.eq(UserStatus.NORMAL);
    }

    private BooleanExpression isPushEventEq(boolean eventCond) {
        return user.profile.isPushEvent.eq(eventCond);
    }
}
