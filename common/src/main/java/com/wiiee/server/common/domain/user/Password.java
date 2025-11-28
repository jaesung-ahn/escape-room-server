package com.wiiee.server.common.domain.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class Password {

    @Column(name = "password")
    private String encodedPassword;

    public static Password of(String rawPassword, PasswordEncoder passwordEncoder) {
        return new Password(passwordEncoder.encode(rawPassword));
    }

    private Password(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }

    public boolean matchesPassword(String rawPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

}
