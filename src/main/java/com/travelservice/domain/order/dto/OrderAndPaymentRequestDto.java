package com.travelservice.domain.order.dto;

import java.util.List;

import com.travelservice.domain.payment.dto.PaymentInfoDto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderAndPaymentRequestDto {
	private List<OrderItemDto> items;
	private PaymentInfoDto payment;
}
