package com.travelservice.domain.product.service;

import java.io.IOException;
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
	private final S3Uploader s3Uploader;

	@Override
	public Product createProduct(AddProductRequest request) throws IOException {
		Region region = regionRepository.findById(request.getRegionId())
			.orElseThrow(() -> new IllegalArgumentException("지역을 찾을 수 없습니다" + request.getRegionId()));

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
			.orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다: " + productId));

		return product;
	}

	@Override
	@Transactional
	public Product updateProduct(Integer productId, UpdateProductRequest request) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));

		Region region = regionRepository.findById(request.getRegionId())
			.orElseThrow(() -> new RuntimeException("지역을 찾을 수 없습니다 " + request.getRegionId()));

		request.applyTo(product, region);

		return product;
	}

	@Override
	@Transactional
	public void deleteProduct(Integer productId) {
		if (!productRepository.existsById(productId)) {
			throw new IllegalArgumentException("존재하지 않는 상품입니다");
		}
		productRepository.deleteById(productId);
	}
}
