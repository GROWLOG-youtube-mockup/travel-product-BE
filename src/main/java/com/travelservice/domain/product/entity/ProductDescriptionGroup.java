package com.travelservice.domain.product.entity;

import java.util.ArrayList;
import java.util.List;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Table(name = "product_description_group")
public class ProductDescriptionGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "group_id")
	private Long groupId;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "type")
	private Integer type;            // 0: 포함사항, 1: 불포함사항, 2: 기타

	@Column(name = "sort_order")
	private Integer sortOrder;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@OneToMany(mappedBy = "descriptionGroup", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductDescriptionItem> descriptionItems = new ArrayList<>();

	@Builder
	public ProductDescriptionGroup(String title, Integer type, Integer sortOrder) {
		this.title = title;
		this.type = type;
		this.sortOrder = sortOrder;
		// this.descriptionItems = descriptionItems;
	}

	public void addItem(ProductDescriptionItem item) {
		if (this.descriptionItems == null) {
			this.descriptionItems = new ArrayList<>();
		}
		this.descriptionItems.add(item);
		item.setDescriptionGroup(this);
	}
}
