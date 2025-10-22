package com.umutyenidil.atlas.controller;

import com.umutyenidil.atlas.dto.request.PaymentRequest;
import com.umutyenidil.atlas.dto.response.BaseResponse;
import com.umutyenidil.atlas.dto.response.PaymentResponse;
import com.umutyenidil.atlas.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<BaseResponse<PaymentResponse>> makePayment(
            @RequestBody @Validated PaymentRequest request
    ) {
        try {
            PaymentResponse response = paymentService.processPayment(request.amount().doubleValue());

            return ResponseEntity.ok(
                    BaseResponse.<PaymentResponse>builder()
                            .status(true)
                            .timestamp(Instant.now())
                            .data(response)
                            .build()
            );

        } catch (RuntimeException ex) {
            return ResponseEntity.status(500).body(
                    BaseResponse.<PaymentResponse>builder()
                            .status(false)
                            .timestamp(Instant.now())
                            .data(PaymentResponse.builder()
                                    .status("FAILED")
                                    .message(ex.getMessage())
                                    .build())
                            .build()
            );
        }
    }
}
