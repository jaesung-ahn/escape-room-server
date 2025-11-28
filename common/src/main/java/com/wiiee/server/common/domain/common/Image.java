package com.wiiee.server.common.domain.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "image", indexes = {})
@Entity
public class Image {

    @Id
    @Column(name = "image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String url;

    public Image(String url) {
        this.url = url;
    }

    public static Image from(String url) {
        return new Image(url);
    }

}
