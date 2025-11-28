package com.wiiee.server.common.domain.event;

import jakarta.validation.constraints.NotNull;
import com.wiiee.server.common.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "event", indexes = {})
@Entity
public class Event extends BaseEntity {

    @Id
    @Column(name = "event_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    // 배너 제목 아래 요약글(제일 상단에서만 필요)
    private String bannerDescription;

    private Boolean isOperated;

    // 시작일자(해당 일자의 00:00 붙여서 저장하기)
    private LocalDateTime startDate;
    // 종료일자(해당 일자의 23:59 붙여서 저장하기)
    private LocalDateTime endDate;

    // 이벤트 위치
    @Enumerated(value = EnumType.STRING)
    private EventLocation eventLocation;

    private Long bannerImgId;

    // 조회수
    private int hitCount = 0;

    @Column(columnDefinition = "TEXT")
    private String eventContent;

    public Event(Boolean isOperated, String title, LocalDateTime startDate, LocalDateTime endDate, EventLocation eventLocation,
                 Long bannerImgId, String eventContent, String bannerDescription) {
        this.isOperated = isOperated;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventLocation = eventLocation;
        this.bannerImgId = bannerImgId;
        this.eventContent = eventContent;
        this.bannerDescription = bannerDescription;
    }

    public void updateEvent(Boolean isOperated, String title, LocalDateTime startDate, LocalDateTime endDate, EventLocation eventLocation,
                            Long bannerImgId, String eventContent, String bannerDescription) {
        this.isOperated = isOperated;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventLocation = eventLocation;
        this.bannerImgId = bannerImgId;
        this.eventContent = eventContent;
        this.bannerDescription = bannerDescription;
    }
}
