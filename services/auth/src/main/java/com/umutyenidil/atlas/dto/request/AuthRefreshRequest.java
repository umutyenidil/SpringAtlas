package com.umutyenidil.atlas.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record AuthRefreshRequest(
        @NotEmpty(message = "{error.jwt.invalid}")
        String refreshToken
) {
}