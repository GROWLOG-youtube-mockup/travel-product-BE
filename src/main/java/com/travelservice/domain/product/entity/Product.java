package com.travelservice.domain.product.entity;

import java.util.ArrayList;
import java.util.List;

import com.travelservice.domain.cart.entity.Cart;
import com.travelservice.global.BaseEntity;
import com.travelservice.global.common.exception.CustomException;
import com.travelservice.global.common.exception.ErrorCode;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Table(name = "products")
public class Product extends BaseEntity {

	@Id  // 이 필드가 기본 키(PK)임을 명시
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 데이터베이스에서 기본 키를 자동 증가 방식으로 생성함
	@Column(name = "product_id")
	private Long productId;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "price")
	private Integer price;

	@Column(name = "total_quantity")
	private Integer totalQuantity;

	@Column(name = "stock_quantity")
	private Integer stockQuantity;

	@Column(name = "description")
	private String description;

	@Column(name = "sale_status")
	private Integer saleStatus;

	@Column(name = "type")
	private Integer type;        // 0: FREE, 1: PACKAGE, 2: SUMMER_VAC, 3: HISTORY, 4: ACTIVITY

	@Column(name = "duration")
	private Integer duration;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "region_id")
	private Region region;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductImage> images = new ArrayList<>();

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<ProductDescriptionGroup> descriptionGroups = new ArrayList<>();

	@OneToMany(mappedBy = "product")
	private List<Cart> cartItems = new ArrayList<>();

	@Builder // 빌더 패턴으로 객체 생성
	public Product(String name, Integer price, Integer totalQuantity, Integer stockQuantity,
		String description, Integer type, Integer saleStatus, Integer duration, Region region) {
		this.name = name;
		this.price = price;
		this.totalQuantity = totalQuantity;
		this.stockQuantity = stockQuantity;
		this.description = description;
		this.type = type;
		this.saleStatus = saleStatus;
		this.duration = duration;
		this.setRegion(region);
	}

	public void update(String name, Integer price, Integer totalQuantity, Integer stockQuantity,
		String description, Integer type, Integer saleStatus, Integer duration, Region region) {
		this.name = name;
		this.price = price;
		this.totalQuantity = totalQuantity;
		this.stockQuantity = stockQuantity;
		this.description = description;
		this.type = type;
		this.saleStatus = saleStatus;
		this.duration = duration;
		this.setRegion(region);
	}

	public void setRegion(Region region) {
		// 기존 region과 관계 끊음
		if (this.region != null) {
			this.region.getProducts().remove(this);
		}
		this.region = region;

		// 새 region에 이 product 추가
		if (region != null && !region.getProducts().contains(this)) {
			region.getProducts().add(this);
		}
	}

	public void addImage(ProductImage image) {
		if (this.images == null) {
			this.images = new ArrayList<>();
		}
		this.images.add(image);
		image.setProduct(this); // 양방향 관계 설정
	}

	public void removeImage(ProductImage image) {
		images.remove(image);
		image.setProduct(null);
	}

	public void clearImages() {
		this.images.clear();
	}

	public void addDescriptionGroup(ProductDescriptionGroup group) {
		if (this.descriptionGroups == null) {
			this.descriptionGroups = new ArrayList<>();
		}
		this.descriptionGroups.add(group);
		group.setProduct(this); // 양방향 관계 설정
	}

	public void clearDescriptionGroups() {
		this.descriptionGroups.clear();
	}

	//재고 관련 메서드
	public int getStock() {
		return this.stockQuantity;
	}

	public void reduceStock(int quantity) {
		if (this.stockQuantity < quantity) {
			throw new CustomException(ErrorCode.OUT_OF_STOCK);
		}
		this.stockQuantity -= quantity;
	}
}
