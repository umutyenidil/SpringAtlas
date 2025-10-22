package com.umutyenidil.atlas.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {
    private String transactionId;
    private String status; // SUCCESS or FAILED
    private String message;
}