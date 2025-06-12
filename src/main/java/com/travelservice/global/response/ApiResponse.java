package com.travelservice.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
	private boolean success;
	private T data;
	private ApiError error;

	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true, data, null);
	}

	public static ApiResponse<?> fail(String code, String message) {
		return new ApiResponse<>(false, null, new ApiError(code, message));
	}
}
