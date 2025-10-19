package com.umutyenidil.atlas.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record RegisterRequest(

        @NotEmpty(message = "{error.auth.validation.email.notempty}")
        @Email(message = "{error.auth.validation.email}")
        String email,

        @NotEmpty(message = "{error.auth.validation.password.notempty}")
        @Size(min = 8, message = "{error.auth.validation.password.size}")
        String password
) {
}
