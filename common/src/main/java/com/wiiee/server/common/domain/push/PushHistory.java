package com.wiiee.server.common.domain.push;

import com.wiiee.server.common.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "push_history", indexes = {})
@Entity
public class PushHistory extends BaseEntity {

    @Id
    @Column(name = "push_history_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String pushContent;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PushType pushType;
    @Column(nullable = false)
    private String targetOs;

    @ColumnDefault("0")
    private int successCnt;
    @ColumnDefault("0")
    private int failCnt;

    private Long eventId;

    public PushHistory(String title, String pushContent, PushType pushType, String targetOs, Long eventId) {
        this.title = title;
        this.pushContent = pushContent;
        this.pushType = pushType;
        this.targetOs = targetOs;
        this.eventId = eventId;
    }

    public void updatePushCnt(int successCnt, int failCnt) {
        this.successCnt = successCnt;
        this.failCnt = failCnt;
    }
}