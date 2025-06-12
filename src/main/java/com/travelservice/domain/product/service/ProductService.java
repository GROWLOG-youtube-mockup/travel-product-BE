package com.travelservice.domain.product.service;

import java.util.List;

import com.travelservice.domain.product.dto.AddProductRequest;
import com.travelservice.domain.product.dto.UpdateProductRequest;
import com.travelservice.domain.product.entity.Product;

public interface ProductService {

	Product createProduct(AddProductRequest request);

	List<Product> getAllProducts();

	Product getProductDetail(Integer productId);

	Product updateProduct(Integer productId, UpdateProductRequest request);

	void deleteProduct(Integer productId);
}
