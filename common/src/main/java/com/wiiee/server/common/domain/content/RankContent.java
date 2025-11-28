package com.wiiee.server.common.domain.content;

import com.wiiee.server.common.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "rank_content", indexes = {})
@Entity
public class RankContent extends BaseEntity {

    @Id
    @Column(name = "rank_content_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private int rank;

    @OneToOne
    @JoinColumn(name="content_id")
    private Content content;

    public RankContent(int rank, Content content) {
        this.rank = rank;
        this.content = content;
    }
}
