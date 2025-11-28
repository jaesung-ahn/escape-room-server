package com.wiiee.server.common.domain.content.favorite;

import com.wiiee.server.common.domain.DefaultEntity;
import com.wiiee.server.common.domain.content.Content;
import com.wiiee.server.common.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Entity
public class ContentFavorite extends DefaultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_favorite_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "content_id",
            referencedColumnName = "content_id",
            nullable = false)
    private Content content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id",
            referencedColumnName = "user_id",
            nullable = false)
    private User user;

    public ContentFavorite(Content content, User user) {
        this.content = content;
        this.user = user;
    }

    public static ContentFavorite of(Content content, User user) {
        return new ContentFavorite(content, user);
    }
}
