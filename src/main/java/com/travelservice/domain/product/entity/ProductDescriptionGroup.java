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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_description_group")

public class ProductDescriptionGroup extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(nullable = false)
	private String title;

	@Column(columnDefinition = "TINYINT")
	private int type;            // 0: 포함사항, 1: 불포함사항, 2: 기타

	private int sortOrder;

	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductDescriptionItem> items = new ArrayList<>();

}
