package com.wiiee.server.api.infrastructure.repository.content.favorite;

import com.wiiee.server.api.application.content.ContentSimpleModel;
import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.common.State;
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
public class ContentFavoriteCustomRepositoryImpl implements ContentFavoriteCustomRepository {

    private final EntityManager em;

    public List<ContentSimpleModel> selectContentFavoriteSimpleModels(Long userId, Pageable pageable) {
        String query =
                "select ct.content_id                        as id, " +
                "       coalesce((select i.url from image i where i.image_id = ct.image_ids[1]), '') as imageUrl, " +
                "       coalesce((select avg(r.rating) from review r where r.content_id = ct.content_id and r.is_approval = true), 0) as ratingAvg, " +
                "       cp.name                              as companyName, " +
                "       cp.state                             as state, " +
                "       cp.city                              as city, " +
                "       ct.name                              as contentName, " +
                "       ct.is_new                            as isNew, " +
                "       ct.is_caution                        as isCaution, " +
                "       ct.play_time                         as playTime " +
                "from content ct " +
                "join company cp on ct.company_id = cp.company_id " +
                "join content_favorite cf on ct.content_id = cf.content_id and user_id = ? " +
                "order by cf.created_at desc " +
                "limit ? offset ?;";

        Query nativeQuery = em.createNativeQuery(query);
        nativeQuery.setParameter(1, userId);
        nativeQuery.setParameter(2, pageable.getPageSize());
        nativeQuery.setParameter(3, pageable.getOffset());

        List<Object[]> results = nativeQuery.getResultList();

        return results.stream()
                .map(row -> new ContentSimpleModel(
                        BigInteger.valueOf(((Number) row[0]).longValue()),  // id
                        (String) row[1],                                      // imageUrl
                        ((BigDecimal) row[2]).doubleValue(),                 // ratingAvg
                        (String) row[3],                                      // companyName
                        (String) row[4],                                      // state
                        (String) row[5],                                      // city
                        (String) row[6],                                      // contentName
                        (Boolean) row[7],                                     // isNew
                        (Boolean) row[8],                                     // isCaution
                        (Integer) row[9]                                      // playTime
                ))
                .collect(Collectors.toList());
    }

}
