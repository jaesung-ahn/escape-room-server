package com.wiiee.server.common.domain.appVersion;

import com.wiiee.server.common.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "app_version")
@Entity
public class AppVersion extends BaseEntity {

    @Id
    @Column(name = "app_version_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10)
    @Enumerated(value = EnumType.STRING)
    private AppOs appOs;

    private String version;
    private Integer versionCode;

    @Column(length = 10)
    @Enumerated(value = EnumType.STRING)
    private SelectionType selectionType;

    // 업데이트 내역
    private String updateContent;

    public AppVersion(AppOs appOs, String version, Integer versionCode, SelectionType selectionType, String updateContent) {
        this.appOs = appOs;
        this.version = version;
        this.versionCode = versionCode;
        this.selectionType = selectionType;
        this.updateContent = updateContent;
    }

    public void updateAppVersion(AppOs appOs, String version, Integer versionCode, SelectionType selectionType, String updateContent) {
        this.appOs = appOs;
        this.version = version;
        this.versionCode = versionCode;
        this.selectionType = selectionType;
        this.updateContent = updateContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppVersion appVersion = (AppVersion) o;
        return id.equals(appVersion.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
