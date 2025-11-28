package com.wiiee.server.api.application.jwt;

import lombok.Value;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Value
public class JWTPostRequestDTO {

    @Email(message = "이메일 양식에 맞지않습니다.")
    @NotBlank(message = "이메일을을 입력하세요.")
    String email;
    @NotBlank(message = "access 토큰을 입력하세요.")
    String accessToken;
    @NotBlank(message = "refresh 토큰을 입력하세요.")
    String refreshToken;

}
