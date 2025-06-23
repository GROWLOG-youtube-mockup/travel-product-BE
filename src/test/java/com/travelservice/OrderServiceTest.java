// package com.travelservice;
//
// import static org.assertj.core.api.Assertions.*;
//
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.transaction.annotation.Transactional;
//
// import com.travelservice.domain.cart.entity.CartItem;
// import com.travelservice.domain.cart.repository.CartItemRepository;
// import com.travelservice.domain.order.dto.OrderItemDto;
// import com.travelservice.domain.order.entity.Order;
// import com.travelservice.domain.order.repository.OrderRepository;
// import com.travelservice.domain.order.service.OrderService;
// import com.travelservice.domain.product.entity.Product;
// import com.travelservice.domain.product.repository.ProductRepository;
// import com.travelservice.domain.user.entity.User;
// import com.travelservice.domain.user.repository.UserRepository;
// import com.travelservice.enums.OrderStatus;
//
// @SpringBootTest
// @Transactional
// public class OrderServiceTest {
//
// 	@Autowired
// 	OrderService orderService;
// 	@Autowired
// 	ProductRepository productRepository;
// 	@Autowired
// 	UserRepository userRepository;
// 	@Autowired
// 	CartItemRepository cartRepo;
// 	@Autowired
// 	private OrderRepository orderRepository;
// 	private User testUser;
//
// 	@BeforeEach
// 	void setUp() {
// 		User existingUser = userRepository.findByEmail("test@example.com");
//
// 		if (existingUser != null) {
// 			testUser = existingUser;
// 		} else {
// 			testUser = userRepository.save(User.builder()
// 				.email("test@example.com")
// 				.name("Test User")
// 				.password("test1234")
// 				.phoneNumber("01012345678")
// 				.roleCode(0)
// 				.build());
// 		}
// 	}
//
// 	@Test
// 	void order_success() {
// 		Order order = Order.builder()
// 			.user(testUser)
// 			.orderDate(LocalDateTime.now())
// 			.totalQuantity(1)
// 			.status(OrderStatus.PENDING)
// 			.build();
//
// 		Order saved = orderRepository.save(order);
//
// 		assertThat(saved.getOrderId()).isNotNull();
// 		assertThat(saved.getUser().getEmail()).isEqualTo("test@example.com");
// 	}
//
// 	@Test
// 	void cart_order_success() {
// 		User user = userRepository.save(User.builder()
// 			.name("카트유저")
// 			.email("cart@test.com")
// 			.password("pw")
// 			.phoneNumber("01000000000")
// 			.roleCode(0)
// 			.build());
//
// 		Product product = productRepository.save(Product.builder()
// 			.name("카트상품")
// 			.price(50000)
// 			.stockQuantity(10)
// 			.totalQuantity(10)
// 			.description("설명")
// 			.saleStatus(1)
// 			.build());
//
// 		cartRepo.save(CartItem.builder()
// 			.user(user)
// 			.product(product)
// 			.quantity(2)
// 			.startDate(LocalDate.now().plusDays(3))
// 			.build());
//
// 		Order order = orderService.createOrderFromCart(user.getEmail());
//
// 		assertThat(order.getOrderId()).isNotNull();
// 		assertThat(order.getItems()).hasSize(1);
// 		assertThat(order.getTotalQuantity()).isEqualTo(2);
// 	}
// }
