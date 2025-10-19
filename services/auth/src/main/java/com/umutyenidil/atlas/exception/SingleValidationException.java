package com.umutyenidil.atlas.exception;

import com.umutyenidil.atlas.dto.ErrorDetail;
import lombok.Getter;

//@Builder
@Getter
public class SingleValidationException extends RuntimeException {
    private final ErrorDetail.Type type;
    private final String subject;
    private final String message;
    private final boolean localized;

    public SingleValidationException(String subject, String message) {
        super(message);
        this.type = ErrorDetail.Type.VALIDATION;
        this.subject = subject;
        this.message = message;
        this.localized = true;
    }

    public SingleValidationException(String subject, String message, boolean localized) {
        super(message);
        this.type = ErrorDetail.Type.VALIDATION;
        this.subject = subject;
        this.message = message;
        this.localized = localized;
    }
}