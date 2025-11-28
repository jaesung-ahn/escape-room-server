package com.wiiee.server.api.application.jwt;


import com.wiiee.server.api.domain.jwt.JWTService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Jwt api")

@RequiredArgsConstructor
@RestController
public class JWTRestController {

    private final JWTService jwtService;

    @Operation(summary = "access token 재발급")
    @PostMapping(value = "/api/jwt/reissue", consumes = APPLICATION_JSON_VALUE)
    private String reissueToken(@Valid @RequestBody JWTPostRequestDTO dto) {
        return jwtService.reissueToken(dto);
    }

}
