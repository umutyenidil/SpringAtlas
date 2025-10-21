package com.umutyenidil.atlas.service;

import com.umutyenidil.atlas.dto.ProductResponse;
import com.umutyenidil.atlas.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    PageResponse<ProductResponse> getProducts(Pageable pageable);
}