package com.travelservice.domain.product.service;

import java.io.IOException;
import java.util.List;

import com.travelservice.domain.product.dto.AddProductRequest;
import com.travelservice.domain.product.dto.ProductDetailResponse;
import com.travelservice.domain.product.dto.ProductListResponse;
import com.travelservice.domain.product.dto.UpdateProductRequest;
import com.travelservice.domain.product.entity.Product;

public interface ProductService {

	ProductDetailResponse createProduct(AddProductRequest request) throws IOException;

	List<ProductListResponse> getAllProducts(Long regionId);

	ProductDetailResponse getProductDetail(Long productId);

	Product updateProduct(Long productId, UpdateProductRequest request) throws
		IOException;

	void deleteProduct(Long productId);
}
