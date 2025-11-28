package com.wiiee.server.api.application.common;

import com.wiiee.server.common.domain.content.Genre;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GenreModel {

    private Integer code;
    private String name;

    public GenreModel(Genre genre) {
        this.code = genre.getCode();
        this.name = genre.getName();
    }
}
