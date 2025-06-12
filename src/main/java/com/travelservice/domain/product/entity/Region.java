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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Table(name = "region")
public class Region extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "region_id", nullable = false)
	private Integer regionId;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "level", nullable = false)
	private Integer level;            // 0: 국가, 1: 광역시/도, 2: 시군구

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Region parent;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Region> children = new ArrayList<>();

	@OneToMany(mappedBy = "region", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Product> products = new ArrayList<>();

	public Region(String name, Integer level, Region parent) {
		this.name = name;
		this.level = level;
		this.parent = parent;
	}

	public List<Product> getProducts() {
		return products;
	}
}
