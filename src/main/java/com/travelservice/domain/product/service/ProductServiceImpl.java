package com.travelservice.domain.product.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.travelservice.domain.product.dto.AddProductRequest;
import com.travelservice.domain.product.dto.UpdateProductRequest;
import com.travelservice.domain.product.entity.Product;
import com.travelservice.domain.product.entity.Region;
import com.travelservice.domain.product.repository.ProductRepository;
import com.travelservice.domain.product.repository.RegionRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final RegionRepository regionRepository;

	@Override
	public Product createProduct(AddProductRequest request) {
		Region region = regionRepository.findById(request.getRegionId())
			.orElseThrow(() -> new IllegalArgumentException("not found region: " + request.getRegionId()));

		Product product = request.toEntity(region);
		return productRepository.save(product);
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

		Region region = regionRepository.findById(request.getRegionId())
			.orElseThrow(() -> new RuntimeException("not found: region " + request.getRegionId()));

		// product.update(request.getName(), request.getPrice(), request.getTotalQuantity(), request.getStockQuantity(),
		// 	request.getDescription(), request.getSaleStatus(), request.getType(), request.getDuration(), region);
		request.applyTo(product, region);

		return product;
	}

	@Override
	@Transactional
	public void deleteProduct(Integer productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다"));
		productRepository.deleteById(productId);
	}
}
