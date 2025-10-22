package com.umutyenidil.atlas.exception;

import com.umutyenidil.atlas.dto.ErrorDetail;
import lombok.Getter;

@Getter
public class SingleException extends RuntimeException {
    private final ErrorDetail.Type type;
    private final String subject;
    private final String message;
    private final boolean localized;

    public SingleException(ErrorDetail.Type type, String subject, String message) {
        super(message);
        this.type = type;
        this.subject = subject;
        this.message = message;
        this.localized = true;
    }

    public SingleException(ErrorDetail.Type type, String subject, String message, boolean localized) {
        super(message);
        this.type = type;
        this.subject = subject;
        this.message = message;
        this.localized = localized;
    }
}