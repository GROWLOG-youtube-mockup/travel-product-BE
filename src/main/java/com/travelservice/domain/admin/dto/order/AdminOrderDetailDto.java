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

import io.swagger.v3.oas.annotations.media.Schema;
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

	@Schema(description = "주문 상태. (PENDING, PAID, CANCELLED) ")
	private String status;

	@Schema(description = "주문 일자")
	@JsonProperty("order_date")
	private LocalDateTime orderDate;

	@Schema(description = "취소 일자(유효한 주문이라면 null값)")
	@JsonProperty("cancel_date")
	private LocalDateTime cancelDate;

	@Schema(description = "상태 변경 일자")
	@JsonProperty("updated_at")
	private LocalDateTime updatedAt;

	@JsonProperty("order_items")
	private List<OrderItemInfo> orderItems;

	private PaymentInfo payment;

	@JsonProperty("total_price")
	private Integer totalPrice;

	// --- 정적 팩토리 메서드 ---
	public static AdminOrderDetailDto from(Order order, Payment payment) {
		User user = order.getUser();
		List<OrderItem> items = order.getOrderItems();

		return AdminOrderDetailDto.builder()
			.orderId(order.getOrderId())
			.user(UserInfo.from(user))
			.status(order.getStatus().name())
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
		@Schema(description = "주문 가격")
		@JsonProperty("price")
		private int price;
		@Schema(description = "상품 수를 의미합니다.(상품 수=인원)")
		@JsonProperty("people_count")
		private int peopleCount;
		@Schema(description = "주문의 총 가격")
		@JsonProperty("total_price")
		private int totalPrice;
		@Schema(description = "주문의 시작 일자")
		@JsonProperty("start_date")
		private LocalDate startDate;

		public static OrderItemInfo from(OrderItem item) {

			Product product = item.getProduct();
			int peopleCount = item.getPeopleCount();
			int price = item.getPrice();
			int totalPrice = price * peopleCount;

			return OrderItemInfo.builder()
				.orderItemId(item.getOrderItemId())
				.product(ProductInfo.from(item.getProduct()))
				.price(price)
				.peopleCount(item.getPeopleCount())
				.totalPrice(totalPrice)
				.startDate(item.getStartDate())
				.build();
		}
	}

	@Getter
	@Builder
	public static class ProductInfo {
		@JsonProperty("product_id")
		private Long productId;
		private String name;

		public static ProductInfo from(Product product) {
			return ProductInfo.builder()
				.productId(product.getProductId())
				.name(product.getName())
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
		@Schema(description = "결제 상태. (PAID, FAILED, CANCELLED)")
		private String status;
		@Schema(description = "결제 일자")
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
			if (cardNumber == null || cardNumber.length() < 4) {
				return "****";
			}
			return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
		}
	}
}
