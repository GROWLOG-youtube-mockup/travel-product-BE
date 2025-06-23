package com.travelservice.global.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	// 공통 에러
	BAD_REQUEST("BAD_REQUEST", "잘못된 요청입니다."),
	INVALID_ACCESSTOKEN("INVALID_ACCESSTOKEN", "유효하지 않은 접근입니다."),
	INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."),

	// Region 관련
	REGION_NOT_FOUND("REGION_NOT_FOUND", "요청하신 지역 정보를 찾을 수 없습니다."),
	// Product 관련
	OUT_OF_STOCK("OUT_OF_STOCK", "재고가 부족합니다."),
	PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다"),
	// Sign in, Log in 관련
	LOGIN_FAILED("LOGIN_FAILED", "아이디 혹은 비밀번호가 올바르지 않습니다."),
	EMAIL_CONFLICT("EMAIL_CONFLICT", "이 이메일로 가입된 계정이 있습니다."),
	EMAIL_NOT_FOUND("EMAIL_NOT_FOUND", "이메일을 찾을 수 없습니다."),
	PHONE_NUMBER_CONFLICT("PHONE_NUMBER_CONFLICT", "이 전화번호로 가입된 계정이 있습니다."),
	PHONE_NUMBER_NOT_FOUND("PHONE_NUMBER_NOT_FOUND", "전화번호를 찾을 수 없습니다."),

	// 인증 관련
	PHONE_VERIFICATION_FAILED("PHONE_VERIFICATION_FAILED", "전화번호 인증에 실패했습니다."),
	EMAIL_VERIFICATION_FAILED("EMAIL_VERIFICATION_FAILED", "이메일 인증에 실패했습니다."),
	PHONE_NUMBER_NOT_VERIFIED("PHONE_NUMBER_NOT_VERIFIED", "전화번호가 인증되지 않았습니다."),
	EMAIL_NOT_VERIFIED("EMAIL_NOT_VERIFIED", "이메일이 인증되지 않았습니다."),
	AUTH_CODE_NOT_FOUND("AUTH_404", "인증번호를 먼저 요청해주세요."),
	INVALID_AUTH_CODE("AUTH_401", "인증번호가 일치하지 않습니다."),

	// User 관련
	USER_NOT_FOUND("USER_NOT_FOUND", "존재하지 않는 회원입니다."),
	UNAUTHORIZED_ACCESS("UNAUTHORIZED_ACCESS", "접근 권한이 없습니다."),
	// Cart 관련
	CART_ITEM_NOT_FOUND("CART_ITEM_NOT_FOUND", "상품을 찾을 수 없습니다."),
	CART_EMPTY("CART_EMPTY", "장바구니에 상품이 없습니다."),
	NO_SELECTED_PRODUCTS("NO_SELECTED_PRODUCTS", "선택된 상품이 없습니다.");

	private final String code;
	private final String message;

}
