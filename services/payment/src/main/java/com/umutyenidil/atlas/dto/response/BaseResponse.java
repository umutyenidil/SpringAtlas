package com.umutyenidil.atlas.dto.response;

import lombok.Builder;

import java.time.Instant;

@Builder
public record BaseResponse<T>(
        boolean status,
        Instant timestamp,
        T data
) {
}