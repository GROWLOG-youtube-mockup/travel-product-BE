package com.travelservice.domain.product.entity;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long productId;

	private String name;

	private String thumbnailImageUrl;

	private int price;

	private int totalQuantity;

	private int stockQuantity;

	@Lob
	private String description;

	private int saleStatus;
}
