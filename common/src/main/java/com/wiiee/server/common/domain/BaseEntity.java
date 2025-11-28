package com.wiiee.server.common.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public class BaseEntity extends DefaultEntity {
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @Column(name = "deleted_at", columnDefinition = "timestamp with time zone")
    protected LocalDateTime deletedAt;
    protected Boolean deleted = false;
}