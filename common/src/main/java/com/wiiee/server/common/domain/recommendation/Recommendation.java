package com.wiiee.server.common.domain.recommendation;

import com.wiiee.server.common.domain.BaseEntity;
import com.wiiee.server.common.domain.content.Content;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recommendation", indexes = {})
@Entity
public class Recommendation extends BaseEntity {

    @Id
    @Column(name = "recommendation_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private RecommendationInfo recommendationInfo;

    @ManyToMany // @OneToMany 는 content_id에 유니크 걸려서 안됨
    @JoinTable(name = "recommendation_contents", //조인테이블명
            joinColumns = @JoinColumn(name="recommendation_id"),  //외래키
            inverseJoinColumns = @JoinColumn(name="content_id") //반대 엔티티의 외래키
    )
    private List<Content> contents = new ArrayList<>();

    public Recommendation(RecommendationInfo recommendationInfo, List<Content> contents) {
        this.recommendationInfo = recommendationInfo;
        this.contents = contents;
    }

    public void addContent(Content content){
        this.contents.add(content);
    }

    public void updateRDT(RecommendationInfo recommendationInfo, List<Content> contents) {
        this.recommendationInfo = recommendationInfo;
        this.contents = contents;
    }
}
