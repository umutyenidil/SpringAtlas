package com.umutyenidil.atlas.seed;

import com.github.javafaker.Faker;
import com.umutyenidil.atlas.entity.Product;
import com.umutyenidil.atlas.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class ProductSeeder implements CommandLineRunner {
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) return;


        Faker faker = new Faker();

        for (int i = 0; i < 100; i++) {
            productRepository.save(Product.builder()
                    .name(faker.commerce().productName())
                    .price(BigDecimal.valueOf(Double.parseDouble(faker.commerce().price())))
                    .build());
        }
    }
}
