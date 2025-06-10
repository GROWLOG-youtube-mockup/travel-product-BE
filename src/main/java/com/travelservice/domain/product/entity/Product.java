package com.travelservice.domain.product.entity;

import java.util.ArrayList;
import java.util.List;

import com.travelservice.global.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product")
public class Product extends BaseEntity {

	@Id  // 이 필드가 기본 키(PK)임을 명시
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 데이터베이스에서 기본 키를 자동 증가 방식으로 생성함
	@Column(name = "product_id")
	private Integer productId;

	@Column(nullable = false)
	private String name;

	private Integer price;

	@Column(name = "total_quantity")
	private Integer totalQuantity;

	@Column(name = "stock_quantity")
	private Integer stockQuantity;

	@Lob
	private String description;

	@Column(columnDefinition = "TINYINT")
	private Integer type;        // 0: FREE, 1: PACKAGE, 2: SUMMER_VAC, 3: HISTORY, 4: ACTIVITY

	@Column(columnDefinition = "TINYINT", nullable = false)
	private Integer saleStatus;  // 0: UPCOMING, 1: ON_SALE, 2: SOLD_OUT

	private Integer duration;

	@ManyToOne(fetch = FetchType.LAZY) // 다대일(N:1) 관계를 나타내며, 지연 로딩 전략 지정.
	@JoinColumn(name = "region_region_id")
	private Region region;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductDescriptionGroup> descriptionGroups = new ArrayList<>();

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("id ASC")
	private List<ProductImage> productImages = new ArrayList<>();
}
