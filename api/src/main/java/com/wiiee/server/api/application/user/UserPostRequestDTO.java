package com.wiiee.server.api.application.user;

import com.wiiee.server.common.domain.user.Password;
import com.wiiee.server.common.domain.user.User;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
public class UserPostRequestDTO {

    private String email;
    private String nickname;
    private String password;

    protected UserPostRequestDTO() {
    }

    public UserPostRequestDTO(String email, String nickname, String password) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
    }

    public User toUser(PasswordEncoder passwordEncoder) {
        return new User(email, nickname, Password.of(password, passwordEncoder));
    }

}
