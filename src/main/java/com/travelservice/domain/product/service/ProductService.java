package com.travelservice.domain.product.service;

import java.io.IOException;
import java.util.List;

import com.travelservice.domain.product.dto.AddProductRequest;
import com.travelservice.domain.product.dto.UpdateProductRequest;
import com.travelservice.domain.product.entity.Product;

public interface ProductService {

	Product createProduct(AddProductRequest request) throws IOException;

	List<Product> getAllProducts();

	Product getProductDetail(Integer productId);

	Product updateProduct(Integer productId, UpdateProductRequest request) throws
		IOException;

	void deleteProduct(Integer productId);
}
