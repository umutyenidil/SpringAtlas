package com.umutyenidil.atlas.mapper;

import com.umutyenidil.atlas.dto.response.TokenResponse;
import com.umutyenidil.atlas.entity.Auth;
import org.springframework.stereotype.Service;

@Service
public class AuthMapper {

    public TokenResponse toAuthResponse(Auth auth) {
        return TokenResponse.builder()
                .build();
    }
}
