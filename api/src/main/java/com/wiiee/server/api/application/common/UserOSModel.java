package com.wiiee.server.api.application.common;

import com.wiiee.server.common.domain.user.UserOS;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserOSModel {

    private Integer code;
    private String name;

    public UserOSModel(UserOS userOS) {
        this.code = userOS.getCode();
        this.name = userOS.getName();
    }

}