package com.umutyenidil.atlas.service.impl;

import com.umutyenidil.atlas.dto.ProductResponse;
import com.umutyenidil.atlas.dto.response.PageResponse;
import com.umutyenidil.atlas.entity.Product;
import com.umutyenidil.atlas.repository.ProductRepository;
import com.umutyenidil.atlas.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultProductService implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public PageResponse<ProductResponse> getProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);

        return this.mapToPageResponse(productPage.map(this::mapToResponse));
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .build();
    }

    private PageResponse<ProductResponse> mapToPageResponse(Page<ProductResponse> page) {
        return PageResponse.<ProductResponse>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

}