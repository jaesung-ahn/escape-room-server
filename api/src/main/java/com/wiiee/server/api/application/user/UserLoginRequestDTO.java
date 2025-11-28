package com.wiiee.server.api.application.user;

import lombok.Getter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Getter
public class UserLoginRequestDTO {

    @Email(message = "이메일 양식에 맞지 않습니다.")
    private String email;
    @NotBlank(message = "패스워드를 입력하세요")
    private String password;

    protected UserLoginRequestDTO() {
    }

    public UserLoginRequestDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

}
