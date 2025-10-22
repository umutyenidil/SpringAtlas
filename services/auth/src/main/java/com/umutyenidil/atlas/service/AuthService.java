package com.umutyenidil.atlas.service;

import com.umutyenidil.atlas.dto.*;
import com.umutyenidil.atlas.dto.request.LoginRequest;
import com.umutyenidil.atlas.dto.response.AuthResponse;
import com.umutyenidil.atlas.dto.response.TokenResponse;
import com.umutyenidil.atlas.entity.Auth;
import com.umutyenidil.atlas.exception.SingleValidationException;
import com.umutyenidil.atlas.exception.UnauthorizedException;
import com.umutyenidil.atlas.mapper.AuthMapper;
import com.umutyenidil.atlas.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RedisBlacklistService redisBlacklistService;
    private final UserDetailsService userDetailsService;

    public TokenResponse login(LoginRequest request) {
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

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public TokenResponse register(RegisterRequest request) {
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

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public TokenResponse refreshToken(String refreshToken) {
        var email = jwtService.extractEmail(refreshToken);
        var auth = authRepository.findByEmail(email);

        if (auth.isEmpty()) throw new UnauthorizedException();

        var claims = new HashMap<String, Object>();

        return TokenResponse.builder()
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

    public AuthResponse validate(String authorizationHeader) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
            throw new UnauthorizedException();

        String jwt = authorizationHeader.substring(7);

        if(redisBlacklistService.contains(jwt)) throw new AuthorizationDeniedException("");

        String email = jwtService.extractEmail(jwt);

        if (email == null) throw new AuthorizationDeniedException("");

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!jwtService.isTokenValid(jwt, userDetails)) throw new AuthorizationDeniedException("");

        var auth = authRepository.findByEmail(email)
                .orElseThrow(UnauthorizedException::new);

        return AuthResponse.builder()
                .userId(auth.getId().toString())
                .build();
    }
}
