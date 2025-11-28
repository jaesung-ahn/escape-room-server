package com.wiiee.server.api.infrastructure.repository.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wiiee.server.api.application.user.UserSnSRequest;
import com.wiiee.server.common.domain.user.User;
import com.wiiee.server.common.domain.wbti.Wbti;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.wiiee.server.common.domain.user.QUser.user;

@RequiredArgsConstructor
@Repository
public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<User> findByUserGetRequestDTO(UserSnSRequest dto) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(user)
                .where(
                        user.email.eq(dto.getEmail()),
                        user.profile.memberType.eq(dto.getMemberType())
                ).fetchOne());
    }

    @Override
    public void saveWbti(Long userId, Wbti wbti) {
        long execute = jpaQueryFactory
                .update(user)
                .set(user.profile.wbti, wbti)
                .where(user.id.eq(userId))
                .execute();
    }
}
