package com.wiiee.server.api.application.common;

import com.wiiee.server.common.domain.content.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DifficultyModel {

    private Integer code;
    private String name;

    public DifficultyModel(Difficulty difficulty) {
        this.code = difficulty.getCode();
        this.name = difficulty.getName();
    }

}
