package com.travelservice.global.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.travelservice.global.common.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice  // 전역 예외 처리기
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ApiResponse<String>> handleCustomException(CustomException ex) {
		return ResponseEntity
			.badRequest()
			.body(ApiResponse.error(ex.getErrorCode()));
	}

	// CustomException에서 지정하지 않은 모든 예외 : 서버 오류로 간주
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex) {
		return ResponseEntity
			.internalServerError()
			.body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
	}
}
