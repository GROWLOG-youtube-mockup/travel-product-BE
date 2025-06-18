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
public class PagedAdminUserResponseDto {
	private List<AdminUserResponseDto> content;
	private Long totalElements;
	private Integer totalPages;
	private Integer currentPage;
}
