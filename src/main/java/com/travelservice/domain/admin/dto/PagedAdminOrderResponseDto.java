package com.travelservice.domain.admin.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagedAdminOrderResponseDto {
	private List<AdminOrderResponseDto> content;
	private Long totalElements;
	private Integer totalPages;
	private Integer currentPage;
}
