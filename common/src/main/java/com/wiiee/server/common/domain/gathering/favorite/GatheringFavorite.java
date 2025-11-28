package com.wiiee.server.common.domain.gathering.favorite;

import com.wiiee.server.common.domain.DefaultEntity;
import com.wiiee.server.common.domain.gathering.Gathering;
import com.wiiee.server.common.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Entity
public class GatheringFavorite extends DefaultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gathering_favorite_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "gathering_id",
            referencedColumnName = "gathering_id",
            nullable = false)
    private Gathering gathering;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id",
            referencedColumnName = "user_id",
            nullable = false)
    private User user;

    public GatheringFavorite(Gathering gathering, User user) {
        this.gathering = gathering;
        this.user = user;
    }

    public static GatheringFavorite of(Gathering gathering, User user) {
        return new GatheringFavorite(gathering, user);
    }
}
