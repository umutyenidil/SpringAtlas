package com.umutyenidil.atlas.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorDetail {

    private Type type;
    private String subject;
    private String message;

    public enum Type {
        AUTH,
        VALIDATION,
        INTERNAL,
        BAD_REQUEST
    }
}