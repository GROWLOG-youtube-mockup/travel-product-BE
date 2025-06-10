package com.travelservice.domain.product.entity;

import java.util.ArrayList;
import java.util.List;

import com.travelservice.global.BaseEntity;

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
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "region")
public class Region extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Integer regionId;

	@Column(nullable = false)
	private String name;

	@Column(columnDefinition = "TINYINT", nullable = false)
	private Integer level;            // 1: 광역시/도, 2: 시군구

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id", nullable = false)
	private Region parent;

	@OneToMany(mappedBy = "parent")
	private List<Region> children = new ArrayList<>();
}
