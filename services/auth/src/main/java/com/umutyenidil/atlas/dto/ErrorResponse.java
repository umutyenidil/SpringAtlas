package com.umutyenidil.atlas.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class ErrorResponse {
    private final Instant timestamp;
    private final boolean success;
    private final List<ErrorDetail> errors;

    public ErrorResponse(List<ErrorDetail> errors) {
        this.timestamp = Instant.now();
        this.success = false;
        this.errors = errors;
    }
}