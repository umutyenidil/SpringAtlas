package com.umutyenidil.atlas.service;

import com.umutyenidil.atlas.dto.*;
import com.umutyenidil.atlas.dto.request.AuthRefreshRequest;
import com.umutyenidil.atlas.dto.request.LoginRequest;
import com.umutyenidil.atlas.dto.response.AuthResponse;
import com.umutyenidil.atlas.entity.Auth;
import com.umutyenidil.atlas.exception.SingleValidationException;
import com.umutyenidil.atlas.exception.UnauthorizedException;
import com.umutyenidil.atlas.mapper.AuthMapper;
import com.umutyenidil.atlas.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final AuthMapper authMapper;
    private final RedisBlacklistService redisBlacklistService;

    public AuthResponse login(LoginRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        var claims = new HashMap<String, Object>();
        var auth = (Auth) authentication.getPrincipal();

        var accessToken = jwtService.generateAccessToken(claims, auth);
        var refreshToken = jwtService.generateRefreshToken(claims, auth);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse register(RegisterRequest request) {
        if (authRepository.findByEmail(request.email()).isPresent())
            throw new SingleValidationException("email", "error.auth.emailexists");

        Auth auth = authRepository.save(
                Auth.builder()
                        .email(request.email())
                        .password(passwordEncoder.encode(request.password()))
                        .build()
        );

        var claims = new HashMap<String, Object>();

        var accessToken = jwtService.generateAccessToken(claims, auth);
        var refreshToken = jwtService.generateRefreshToken(claims, auth);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse refreshToken(String refreshToken) {
        var email = jwtService.extractEmail(refreshToken);
        var auth = authRepository.findByEmail(email);

        if (auth.isEmpty()) throw new UnauthorizedException();

        var claims = new HashMap<String, Object>();

        return AuthResponse.builder()
                .accessToken(jwtService.generateAccessToken(claims, auth.get()))
                .refreshToken(jwtService.generateRefreshToken(claims, auth.get()))
                .build();
    }

    public void logout(String token) {
        var email = jwtService.extractEmail(token);
        var auth = authRepository.findByEmail(email);

        if (auth.isEmpty()) throw new UnauthorizedException();

        var expiration = jwtService.extractExpiration(token);

        redisBlacklistService.add(token, expiration.toInstant());
    }
}
