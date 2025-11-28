package com.wiiee.server.common.domain.wbti;

import jakarta.validation.constraints.NotNull;
import com.wiiee.server.common.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "wbti", indexes = {})
@Entity
public class Wbti extends BaseEntity {

    @Id
    @Column(name = "wbti_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    // 위비티아이 이미지 id
    private Long wbtiImageId;

    private String tags;

    private String descriptions;

    @ManyToMany
    @JoinTable(name = "wbti_partners", //조인테이블명
            joinColumns = @JoinColumn(name="wbti_id"),  //외래키
            inverseJoinColumns = @JoinColumn(name="partner_id") //반대 엔티티의 외래키
    )
    private List<Wbti> wbtiPartners = new ArrayList<>();

    public Wbti(String name, Long wbtiImageId, String tags, String descriptions) {
        this.name = name;
        this.wbtiImageId = wbtiImageId;
        this.tags = tags;
        this.descriptions = descriptions;
    }

    public Wbti(Long id) {
        this.id = id;
    }

    public void addPartner(Wbti partner){
        this.wbtiPartners.add(partner);
    }

    public void updateWbti(String name, Long wbtiImageId, String tags, String descriptions, List<Wbti> wbtiPartners) {
        this.name = name;
        this.wbtiImageId = wbtiImageId;
        this.tags = tags;
        this.descriptions = descriptions;
        this.wbtiPartners = wbtiPartners;
    }
}
