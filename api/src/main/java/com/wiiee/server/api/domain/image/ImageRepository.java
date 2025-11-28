package com.wiiee.server.api.domain.image;

import com.wiiee.server.common.domain.common.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    @Query("select i from Image i where i.id in :ids")
    List<Image> findByIdsIn(@Param("ids")List<Long> ids);

}
