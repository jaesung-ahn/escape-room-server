package com.wiiee.server.common.domain.recommendation;

import com.wiiee.server.common.domain.BaseEntity;
import com.wiiee.server.common.domain.wbti.Wbti;
import com.wiiee.server.common.domain.content.Content;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "wbti_recommendation", indexes = {})
@Entity
public class WbtiRecommendation extends BaseEntity {

    @Id
    @Column(name = "wbti_recommendation_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "wbti_id",
            referencedColumnName = "wbti_id",
            nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Wbti wbti;

    @ManyToMany
    @JoinTable(name = "wbti_recommendation_contents", //조인테이블명
            joinColumns = @JoinColumn(name="wbti_recommendation_id"),  //외래키
            inverseJoinColumns = @JoinColumn(name="content_id") //반대 엔티티의 외래키
    )
    private List<Content> contents = new ArrayList<>();

    public WbtiRecommendation(List<Content> contents) {
        this.contents = contents;
    }

}
