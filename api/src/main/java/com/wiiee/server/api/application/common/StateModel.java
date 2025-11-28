package com.wiiee.server.api.application.common;

import com.wiiee.server.common.domain.common.State;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StateModel {
    private Integer code;
    private String name;
    private String shortName;
    private Boolean isActive;
    private List<CityModel> children;

    public StateModel(State state) {
        this.code = state.getCode();
        this.name = state.getName();
        this.isActive = State.isServiceState(state);
        this.shortName = state.getShortName();
        this.children = state.getCityCodes().stream().map(CityModel::new).collect(Collectors.toList());
    }
}
