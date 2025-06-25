package com.travelservice.global.common;

import com.travelservice.global.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
	private final boolean success;
	private final T data;
	private Error error;

	public static <T> ApiResponse<T> ok(T data) {
		return new ApiResponse<>(true, data, null);
	}

	public static <T> ApiResponse<T> error(ErrorCode errorCode) {
		return new ApiResponse<>(false, null, new Error(errorCode));
	}

	@Getter
	public static class Error {
		private String code;
		private String message;

		public Error(ErrorCode errorCode) {
			this.code = errorCode.getCode();
			this.message = errorCode.getMessage();
		}
	}
}
