package com.wiiee.server.api.infrastructure.repository.gathering;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wiiee.server.common.domain.gathering.Gathering;
import com.wiiee.server.common.domain.gathering.request.GatheringRequest;
import com.wiiee.server.common.domain.gathering.request.GatheringRequestStatus;
import com.wiiee.server.common.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.wiiee.server.common.domain.gathering.request.QGatheringRequest.gatheringRequest;

@RequiredArgsConstructor
@Repository
public class GatheringRequestCustomRepositoryImpl implements GatheringRequestCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<GatheringRequest> findAllByGathering(Gathering gathering) {

        List<GatheringRequestStatus> gatheringRequestStatuses = new ArrayList<>();
        gatheringRequestStatuses.add(GatheringRequestStatus.UNVERIFIED);
        gatheringRequestStatuses.add(GatheringRequestStatus.VERIFIED);

        return jpaQueryFactory.selectFrom(gatheringRequest)
                .where(gatheringRequest.gathering.eq(gathering),
                        gatheringRequest.gatheringRequestStatus.in(gatheringRequestStatuses)
                        )
                .orderBy(gatheringRequest.createdAt.desc())
                .fetch();
    }

    @Override
    public long countUnverifiedRequestByGathering(Gathering gathering) {

        return jpaQueryFactory.selectFrom(gatheringRequest)
                .where(gatheringRequest.gathering.eq(gathering),
                        gatheringRequest.gatheringRequestStatus.eq(GatheringRequestStatus.UNVERIFIED))
                .fetchCount();
    }

    @Override
    public GatheringRequest findApprovedGatheringRequest(Gathering gathering, User user) {

        return jpaQueryFactory.selectFrom(gatheringRequest)
                .where(gatheringRequest.gathering.eq(gathering),
                        gatheringRequest.gatheringRequestStatus.eq(GatheringRequestStatus.APPROVAL),
                        gatheringRequest.requestUser.eq(user))
                .fetchOne();
    }

}
