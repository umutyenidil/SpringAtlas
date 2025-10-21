package com.umutyenidil.atlas.controller;

import com.umutyenidil.atlas.dto.ProductResponse;
import com.umutyenidil.atlas.dto.response.BaseResponse;
import com.umutyenidil.atlas.dto.response.PageResponse;
import com.umutyenidil.atlas.service.ProductService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<ProductResponse>>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        var response = productService.getProducts(pageable);

        return ResponseEntity.ok(
                BaseResponse.<PageResponse<ProductResponse>>builder()
                        .status(true)
                        .timestamp(Instant.now())
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductResponse>> getProduct(
            @PathVariable("id") String id
    ) {
        return ResponseEntity.ok(
                BaseResponse.<ProductResponse>builder()
                        .status(true)
                        .timestamp(Instant.now())
                        .data(
                                ProductResponse.builder()
                                        .id(id)
                                        .name("Test Product")
                                        .price(BigDecimal.valueOf(1439.99))
                                        .build()
                        )
                        .build()
        );
    }
}
