package com.umutyenidil.atlas.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record LoginRequest(
        @NotEmpty(message = "{error.auth.validation.email.notempty}")
        @Email(message = "{error.auth.validation.email}")
        String email,
        @NotEmpty(message = "{error.auth.validation.password.notempty}")
        String password
) {
}