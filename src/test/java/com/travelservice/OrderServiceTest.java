package com.travelservice;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.travelservice.domain.cart.entity.CartItem;
import com.travelservice.domain.cart.repository.CartItemRepository;
import com.travelservice.domain.order.dto.OrderItemDto;
import com.travelservice.domain.order.entity.Order;
import com.travelservice.domain.order.service.OrderService;
import com.travelservice.domain.product.entity.Product;
import com.travelservice.domain.product.repository.ProductRepository;
import com.travelservice.domain.user.entity.User;
import com.travelservice.domain.user.repository.UserRepository;

@SpringBootTest
public class OrderServiceTest {

	@Autowired
	OrderService orderService;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	CartItemRepository cartRepo;

	@Test
	void order_success() {
		// given
		User user = userRepository.save(User.builder()
			.name("Test User")
			.email("test@example.com")
			.password("test1234")
			.phoneNumber("01012345678")
			.roleCode(0)
			.build());

		Product product = productRepository.save(Product.builder()
			.name("테스트 상품")
			.price(100000)
			.stockQuantity(10)
			.totalQuantity(10)
			.description("설명")
			.saleStatus(1)
			.build());

		List<OrderItemDto> items = List.of(
			new OrderItemDto(
				product.getProductId(),
				2,
				LocalDate.now().plusDays(3)
			)
		);

		// when
		Order order = orderService.createOrder(user.getEmail(), items);

		// then
		assertThat(order.getOrderId()).isNotNull();
	}

	@Test
	void cart_order_success() {
		User user = userRepository.save(User.builder()
			.name("카트유저")
			.email("cart@test.com")
			.password("pw")
			.phoneNumber("01000000000")
			.roleCode(0)
			.build());

		Product product = productRepository.save(Product.builder()
			.name("카트상품")
			.price(50000)
			.stockQuantity(10)
			.totalQuantity(10)
			.description("설명")
			.saleStatus(1)
			.build());

		cartRepo.save(CartItem.builder()
			.user(user)
			.product(product)
			.quantity(2)
			.startDate(LocalDate.now().plusDays(3))
			.build());

		Order order = orderService.createOrderFromCart(user.getEmail());

		assertThat(order.getOrderId()).isNotNull();
		assertThat(order.getItems()).hasSize(1);
		assertThat(order.getTotalQuantity()).isEqualTo(2);
	}
}
