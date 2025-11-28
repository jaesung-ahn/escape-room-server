package com.wiiee.server.api.domain.jwt;

import com.wiiee.server.api.application.jwt.JWTPostRequestDTO;
import com.wiiee.server.api.domain.user.UserService;
import com.wiiee.server.api.infrastructure.jwt.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneId;

@RequiredArgsConstructor
@Service
public class JWTService {

    private final UserService userService;

    private final JwtTokenProvider jwtTokenProvider;

    public String reissueToken(JWTPostRequestDTO dto) {
        try {
            jwtTokenProvider.getEmailFromToken(dto.getAccessToken());
            throw new IllegalArgumentException("만료되지 않은 access token 입니다.");
        } catch (ExpiredJwtException accessExpiredJwtException) {
            final var findUser = userService.findByEmail(dto.getEmail()).orElseThrow();

            try {
                if (!findUser.getRefreshToken().equals(dto.getRefreshToken())) {
                    throw new IllegalArgumentException("잘못된 refresh token 입니다.");
                };
                jwtTokenProvider.getExpFromToken(dto.getRefreshToken()).toInstant().atZone(ZoneId.systemDefault());

                return jwtTokenProvider.createToken(dto.getEmail()).getAccessToken();
            } catch (ExpiredJwtException refreshExpiredJwtException) {
                throw new IllegalArgumentException("refresh token 만료되었습니다.");
            }

        }
    }

}
