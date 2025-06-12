package com.travelservice.domain.product.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.travelservice.domain.product.dto.AddProductRequest;
import com.travelservice.domain.product.dto.UpdateProductRequest;
import com.travelservice.domain.product.entity.Product;
import com.travelservice.domain.product.repository.ProductRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;

	@Override
	public Product createProduct(AddProductRequest request) {
		return productRepository.save(request.toEntity());
	}

	@Override
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	@Override
	public Product getProductDetail(Integer productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new EntityNotFoundException("해당 상품을 찾을 수 없습니다"));

		return product;
	}

	@Override
	@Transactional
	public Product updateProduct(Integer productId, UpdateProductRequest request) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new IllegalArgumentException("not found: " + productId));
		product.update(request.getName(), request.getPrice(), request.getTotalQuantity(), request.getStockQuantity(),
			request.getDescription(), request.getSaleStatus(), request.getType(), request.getDuration());

		return product;
	}

	@Override
	public void deleteProduct(Integer productId) {
		productRepository.deleteById(productId);
	}
}