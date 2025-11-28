package com.wiiee.server.common.domain.notice;

import com.wiiee.server.common.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notice", indexes = {})
@Entity
public class Notice extends BaseEntity {

    @Id
    @Column(name = "notice_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "boolean default true")
    private Boolean isOperated;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String noticeContent;

    public Notice(String title, String noticeContent) {
        this.title = title;
        this.noticeContent = noticeContent;
    }
}
