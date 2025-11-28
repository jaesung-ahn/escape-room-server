package com.wiiee.server.api.application.security;

import com.wiiee.server.api.infrastructure.jwt.JwtTokenProvider;
import com.wiiee.server.api.domain.user.UserRepository;
import com.wiiee.server.common.domain.user.User;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class SecurityAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtTokenProvider.getJwtFromRequest(request);
        String refreshToken = null;

        try {
            if (StringUtils.isNoneBlank(accessToken) && jwtTokenProvider.validateToken(accessToken)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException e) {
            Optional<User> userOpt = userRepository.findByEmail(jwtTokenProvider.getEmailFromToken(accessToken));
            if(userOpt.isPresent()) {
                refreshToken = userOpt.get().getRefreshToken();
            }
            log.error("ACCESS TOKEN EXPIRED {} | {}", accessToken, e.getMessage());
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            log.error("JWT FILTER INTERNAL ERROR : {} | {}", accessToken, e.getMessage());
            e.printStackTrace();
            return;
        }


//        if (StringUtils.isNotBlank(refreshToken)) {
//            try {
//                try {
//                    if(jwtTokenProvider.validateToken(refreshToken)) {
//                        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
//                        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//                        response.setHeader("new-access-token", jwtTokenProvider.createToken(jwtTokenProvider.getClaims(refreshToken, "email")).getAccessToken());
//                    }
//                } catch (ExpiredJwtException e) {
//                    SecurityContextHolder.clearContext();
//                    log.error("ACCESS TOKEN EXPIRED {} | {}", refreshToken, e.getMessage());
//                }
//            } catch (Exception e) {
//                SecurityContextHolder.clearContext();
//                log.error("JWT FILTER INTERNAL ERROR : {} | {}", accessToken, e.getMessage());
//                return;
//            }
//        }

        filterChain.doFilter(request, response);
    }
}

