package com.umutyenidil.atlas.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
@Builder
public class Product {

    @Id
    private String id;

    private String name;

    private BigDecimal price;

    @Builder.Default
    private Instant createdAt = Instant.now();
}