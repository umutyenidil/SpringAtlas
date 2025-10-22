package com.umutyenidil.atlas.service;

import com.umutyenidil.atlas.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse processPayment(double amount) throws RuntimeException;
}