package com.wiiee.server.common.domain.content;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class ContentBasicInfo {

    private String name;

    private Boolean isOperated;

    @Type(ListArrayType.class)
    @Column(columnDefinition = "bigint[]")
    private List<Long> imageIds = new ArrayList<>();

    // 놀거리 new 표시 여부
    private Boolean isNew;

    // 놀거리 new 표시 만료 날짜(관리자가 수동으로 날짜 선택)
    private LocalDate newDisplayExpirationDate;

    @Enumerated(value = EnumType.STRING)
    private Genre genre;

    private Integer minPeople;

    private Integer maxPeople;

    // 난이도
    @Enumerated(value = EnumType.STRING)
    private Difficulty difficulty;

    // 활동성
    @Enumerated(value = EnumType.STRING)
    private ActivityLevel activityLevel;

    // 유형(자물쇠, 장치)
    @Enumerated(value = EnumType.STRING)
    private EscapeType escapeType;

    // 유형(자물쇠, 장치) 해당 비율 없음
    private Boolean isNoEscapeType = false;

    private Integer playTime;

    @Column(length = 800)
    private String information;

    private Boolean isCaution;

    // tag 삭제
//    @JoinTable(name = "content_tag",
//            joinColumns = @JoinColumn(name = "content_id", referencedColumnName = "content_id", nullable = false),
//            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "tag_id", nullable = false))
//    @ManyToMany(fetch = EAGER, cascade = PERSIST)
//    private Set<Tag> tags = new HashSet<>();

    public ContentBasicInfo(String name, Genre genre, String information, Integer playTime, ActivityLevel activityLevel,
                            EscapeType escapeType, Boolean isCaution, Integer minPeople, Integer maxPeople,
                            Difficulty difficulty, List<Long> imageIds, Boolean isNoEscapeType, Boolean isNew,
                            LocalDate newDisplayExpirationDate, Boolean isOperated) {
        this.name = name;
        this.genre = genre;
        this.information = information;
        this.playTime = playTime;
        this.activityLevel = activityLevel;
        this.escapeType = escapeType;
        this.isCaution = isCaution;
        this.isNoEscapeType = isNoEscapeType;
        this.minPeople = minPeople;
        this.maxPeople = maxPeople;
        this.difficulty = difficulty;
        this.imageIds = imageIds;
        this.isNew = isNew;
        this.newDisplayExpirationDate = newDisplayExpirationDate;
        this.isOperated = isOperated;
    }

    public ContentBasicInfo(String name) {
        this.name = name;
    }
}
