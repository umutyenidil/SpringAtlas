package com.umutyenidil.atlas.controller;

import com.umutyenidil.atlas.dto.response.AuthResponse;
import com.umutyenidil.atlas.dto.request.LoginRequest;
import com.umutyenidil.atlas.dto.RegisterRequest;
import com.umutyenidil.atlas.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody @Valid LoginRequest request
    ) {
        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody @Valid RegisterRequest request
    ) {
        AuthResponse response = authService.register(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @RequestHeader("Authorization") String bearerToken
    ) {
        AuthResponse response = authService.refreshToken(bearerToken.substring(7));

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader("Authorization") String bearerToken
    ) {
        authService.logout(bearerToken.substring(7));

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/hello-world")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello World!");
    }
}
