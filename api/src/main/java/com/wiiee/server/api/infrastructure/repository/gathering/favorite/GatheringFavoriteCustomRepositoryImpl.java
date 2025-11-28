package com.wiiee.server.api.infrastructure.repository.gathering.favorite;

import com.wiiee.server.api.application.gathering.favorite.GatheringFavoriteSimpleModel;
import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.common.State;
import com.wiiee.server.common.domain.gathering.GatheringStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GatheringFavoriteCustomRepositoryImpl implements GatheringFavoriteCustomRepository {

    private final EntityManager em;

    public List<GatheringFavoriteSimpleModel> selectGatheringFavoriteSimpleModels(Long userId, Pageable pageable) {
        String query =
                "select " +
                    "g.gathering_id              as id, " +
                    "g.title                     as title, " +
                    "g.max_people                as maxPeople, " +
                    "g.gathering_status          as gatheringStatusName, " +
                    "(select count(m.gathering_member_id) from gathering_member m where m.gathering_id = g.gathering_id and m.status = 'APPROVAL') as currentPeople, " +

                    "(select i.url from image i where i.image_id = ct.image_ids[1]) as imageUrl, " +
                    "ct.name                     as contentName, " +
                    "cp.state                    as state, " +
                    "cp.city                     as city, " +
                    "coalesce((select avg(r.rating) from review r where r.content_id = ct.content_id and r.is_approval = true), 0) as ratingAvg, " +
                    "cp.name                     as companyName " +
                "from gathering g " +
                "join gathering_favorite gf on g.gathering_id = gf.gathering_id and gf.user_id = ? " +
                "join content ct on g.content_id = ct.content_id " +
                "join company cp on ct.company_id = cp.company_id " +
                "order by gf.created_at desc " +
                "limit ? offset ? ";

        Query nativeQuery = em.createNativeQuery(query);
        nativeQuery.setParameter(1, userId);
        nativeQuery.setParameter(2, pageable.getPageSize());
        nativeQuery.setParameter(3, pageable.getOffset());

        List<Object[]> results = nativeQuery.getResultList();

        return results.stream()
                .map(row -> new GatheringFavoriteSimpleModel(
                        BigInteger.valueOf(((Number) row[0]).longValue()), // id
                        (String) row[1],                                     // title
                        (Integer) row[2],                                    // maxPeople
                        (String) row[3],                                     // gatheringStatus
                        (BigInteger) row[4],                                 // currentPeople
                        (String) row[5],                                     // imageUrl
                        (String) row[6],                                     // contentName
                        (String) row[7],                                     // state
                        (String) row[8],                                     // city
                        ((BigDecimal) row[9]).doubleValue(),                // ratingAvg
                        (String) row[10]                                     // companyName
                ))
                .collect(Collectors.toList());
    }
}
