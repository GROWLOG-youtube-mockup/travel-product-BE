package com.travelservice.domain.admin.dto.order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.travelservice.domain.order.entity.Order;
import com.travelservice.domain.order.entity.OrderItem;
import com.travelservice.domain.payment.entity.Payment;
import com.travelservice.domain.product.entity.Product;
import com.travelservice.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminOrderDetailDto {
	@JsonProperty("order_id")
	private Long orderId;

	private UserInfo user;

	private String status;

	@JsonProperty("total_quantity")
	private Integer totalQuantity;

	@JsonProperty("order_date")
	private LocalDateTime orderDate;

	@JsonProperty("cancel_date")
	private LocalDateTime cancelDate;

	@JsonProperty("updated_at")
	private LocalDateTime updatedAt;

	@JsonProperty("order_items")
	private List<OrderItemInfo> orderItems;

	private PaymentInfo payment;

	// --- 정적 팩토리 메서드 ---
	public static AdminOrderDetailDto from(Order order, Payment payment) {
		User user = order.getUser();
		List<OrderItem> items = order.getItems();

		return AdminOrderDetailDto.builder()
			.orderId(order.getOrderId())
			.user(UserInfo.from(user))
			.status(order.getStatus().name())
			.totalQuantity(order.getTotalQuantity())
			.orderDate(order.getOrderDate())
			.cancelDate(order.getCancelDate())
			.updatedAt(order.getUpdatedAt())
			.orderItems(items.stream().map(OrderItemInfo::from).collect(Collectors.toList()))
			.payment(payment != null ? PaymentInfo.from(payment) : null)
			.build();
	}

	// --- 내부 정적 클래스들 ---
	@Getter
	@Builder
	public static class UserInfo {
		@JsonProperty("user_id")
		private Long userId;
		private String name;
		private String email;
		@JsonProperty("phone_number")
		private String phoneNumber;

		public static UserInfo from(User user) {
			return UserInfo.builder()
				.userId(user.getUserId())
				.name(user.getName())
				.email(user.getEmail())
				.phoneNumber(user.getPhoneNumber())
				.build();
		}
	}

	@Getter
	@Builder
	public static class OrderItemInfo {
		@JsonProperty("order_item_id")
		private Long orderItemId;
		private ProductInfo product;
		@JsonProperty("people_count")
		private int peopleCount;
		@JsonProperty("start_date")
		private LocalDate startDate;
		@JsonProperty("created_at")
		private LocalDateTime createdAt;
		@JsonProperty("updated_at")
		private LocalDateTime updatedAt;

		public static OrderItemInfo from(OrderItem item) {
			return OrderItemInfo.builder()
				.orderItemId(item.getOrderItemId())
				.product(ProductInfo.from(item.getProduct()))
				.peopleCount(item.getPeopleCount())
				.startDate(item.getStartDate())
				.updatedAt(null)
				.build();
		}
	}

	@Getter
	@Builder
	public static class ProductInfo {
		@JsonProperty("product_id")
		private Long productId;
		private String name;
		private Integer price;

		public static ProductInfo from(Product product) {
			return ProductInfo.builder()
				.productId(product.getProductId())
				.name(product.getName())
				.price(product.getPrice())
				.build();
		}
	}

	@Getter
	@Builder
	public static class PaymentInfo {
		@JsonProperty("payment_id")
		private Long paymentId;
		@JsonProperty("card_number")
		private String cardNumber;
		private String status;
		@JsonProperty("payment_datetime")
		private LocalDateTime paidAt;

		public static PaymentInfo from(Payment payment) {
			return PaymentInfo.builder()
				.paymentId(payment.getPaymentId())
				.cardNumber(maskCardNumber(payment.getCardNumber()))
				.status(payment.getStatus() != null ? payment.getStatus().name() : null)
				.paidAt(payment.getPaidAt())
				.build();
		}

		// 카드번호 마스킹 함수
		private static String maskCardNumber(String cardNumber) {
			if (cardNumber == null || cardNumber.length() < 4)
				return "****";
			return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
		}
	}
}
