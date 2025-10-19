package com.umutyenidil.atlas.mapper;

import com.umutyenidil.atlas.dto.response.AuthResponse;
import com.umutyenidil.atlas.entity.Auth;
import org.springframework.stereotype.Service;

@Service
public class AuthMapper {

    public AuthResponse toAuthResponse(Auth auth) {
        return AuthResponse.builder()
                .build();
    }
}
