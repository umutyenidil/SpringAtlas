package com.umutyenidil.atlas.service.impl;

import com.umutyenidil.atlas.dto.ErrorDetail;
import com.umutyenidil.atlas.dto.response.PaymentResponse;
import com.umutyenidil.atlas.exception.SingleException;
import com.umutyenidil.atlas.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultPaymentService implements PaymentService {

    private final Random random = new Random();

    @Override
    public PaymentResponse processPayment(double amount) {
        boolean success = random.nextBoolean();

        if (!success) {
            throw new SingleException(ErrorDetail.Type.PAYMENT, "payment", "Payment failed");
        }

        return PaymentResponse.builder()
                .transactionId(UUID.randomUUID().toString())
                .status("SUCCESS")
                .message("Payment of $" + amount + " completed successfully.")
                .build();
    }
}