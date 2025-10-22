package com.umutyenidil.atlas.controller;

import com.umutyenidil.atlas.dto.response.AuthResponse;
import com.umutyenidil.atlas.dto.response.TokenResponse;
import com.umutyenidil.atlas.dto.request.LoginRequest;
import com.umutyenidil.atlas.dto.RegisterRequest;
import com.umutyenidil.atlas.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @RequestBody @Valid LoginRequest request
    ) {
        TokenResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(
            @RequestBody @Valid RegisterRequest request
    ) {
        TokenResponse response = authService.register(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @RequestHeader("Authorization") String bearerToken
    ) {
        TokenResponse response = authService.refreshToken(bearerToken.substring(7));

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader("Authorization") String bearerToken
    ) {
        authService.logout(bearerToken.substring(7));

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validate")
    public ResponseEntity<AuthResponse> validate(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header
    ) {
        return ResponseEntity.ok(authService.validate(header));
    }
}
