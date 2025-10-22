package com.umutyenidil.atlas.dto.response;

import lombok.Builder;

@Builder
public record AuthResponse(
        String userId
) {
}
