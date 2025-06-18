package com.travelservice.domain.cart.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.travelservice.domain.cart.dto.AddToCartRequest;
import com.travelservice.domain.cart.dto.GetCartItemResponse;
import com.travelservice.domain.cart.entity.Cart;
import com.travelservice.domain.cart.repository.CartRepository;
import com.travelservice.domain.product.entity.Product;
import com.travelservice.domain.product.entity.Region;
import com.travelservice.domain.product.repository.ProductRepository;
import com.travelservice.domain.user.entity.User;
import com.travelservice.global.common.exception.CustomException;
import com.travelservice.global.common.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

	@Mock
	private CartRepository cartRepository;
	@Mock
	private ProductRepository productRepository;
	@InjectMocks
	private CartService cartService;

	@Test
	public void addToCart_성공() {
		// given
		User user = User.builder().userId(1L).name("테스트 유저").email("abc@gamil.com")
			.password("123").phoneNumber("000").roleCode(0).build();
		Product product = Product.builder().name("테스트 상품").price(1000)
			.totalQuantity(100).stockQuantity(90).description(" ").type(1)
			.saleStatus(1).duration(3).region(new Region("광주", 1, null)).build();
		product.setProductId(99L);
		productRepository.save(product);

		AddToCartRequest request = new AddToCartRequest(99L, 2, LocalDate.now());

		given(productRepository.findById(99L)).willReturn(Optional.of(product));

		// when
		cartService.addToCart(user, request);
		// then
		then(cartRepository).should().save(any(Cart.class));
	}

	@Test
	public void addToCart_실패() {
		//gien
		User user = User.builder().userId(1L).name("테스트 유저").email("abc@gamil.com")
			.password("123").phoneNumber("000").roleCode(0).build();
		Product product = Product.builder().name("테스트 상품").price(1000)
			.totalQuantity(100).stockQuantity(90).description(" ").type(1)
			.saleStatus(1).duration(3).region(new Region("광주", 1, null)).build();
		product.setProductId(99L);

		AddToCartRequest request = new AddToCartRequest(99L, 2, LocalDate.now());

		given(productRepository.findById(product.getProductId())).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> cartService.addToCart(user, request))
			.isInstanceOf(CustomException.class)
			.hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
	}

	@Test
	public void getCartItems_성공() {
		User user = User.builder().userId(1L).name("테스트 유저").email("abc@gamil.com")
			.password("123").phoneNumber("000").roleCode(0).build();
		Product product = Product.builder().name("테스트 상품").price(1000)
			.totalQuantity(100).stockQuantity(90).description(" ").type(1)
			.saleStatus(1).duration(3).region(new Region("광주", 1, null)).build();
		product.setProductId(99L);

		Cart cart = Cart.builder()
			.user(user)
			.product(product)
			.quantity(2)
			.startDate(LocalDate.of(2025, 01, 01)).build();

		given(cartRepository.findByUser_UserId(user.getUserId())).willReturn(List.of(cart));

		// when
		List<GetCartItemResponse> result = cartService.getCartItems(user.getUserId());

		// then
		assertThat(result).hasSize(1);
		GetCartItemResponse response = result.get(0);
		assertThat(response.getCartItemId()).isEqualTo(cart.getId());
		assertThat(response.getProductId()).isEqualTo(product.getProductId());
		assertThat(response.getProductName()).isEqualTo(product.getName());
		assertThat(response.getQuantity()).isEqualTo(cart.getQuantity());
		assertThat(response.getStartDate()).isEqualTo(cart.getStartDate());
		assertThat(response.getPrice()).isEqualTo(product.getPrice());
		assertThat(response.getTotalPrice()).isEqualTo(product.getPrice() * cart.getQuantity());
	}

	@Test
	void getCartItems_여러개아이템_성공() {
		User user = User.builder().userId(1L).name("테스트 유저").email("abc@gamil.com")
			.password("123").phoneNumber("000").roleCode(0).build();
		Product product1 = Product.builder().name("테스트 상품1").price(1000)
			.totalQuantity(100).stockQuantity(90).description(" ").type(1)
			.saleStatus(1).duration(3).region(new Region("광주", 1, null)).build();
		product1.setProductId(99L);
		Product product2 = Product.builder().name("테스트 상품2").price(1000)
			.totalQuantity(100).stockQuantity(90).description(" ").type(1)
			.saleStatus(1).duration(3).region(new Region("경주", 1, null)).build();
		product2.setProductId(991L);

		Cart cart1 = Cart.builder()
			.user(user)
			.product(product1)
			.quantity(2)
			.startDate(LocalDate.of(2025, 01, 01)).build();
		Cart cart2 = Cart.builder()
			.user(user)
			.product(product2)
			.quantity(1)
			.startDate(LocalDate.of(2026, 01, 01)).build();

		given(cartRepository.findByUser_UserId(user.getUserId())).willReturn(List.of(cart1, cart2));

		//when
		List<GetCartItemResponse> result = cartService.getCartItems(user.getUserId());

		// then
		assertThat(result).hasSize(2);

		GetCartItemResponse item1 = result.get(0);
		assertThat(item1.getProductId()).isEqualTo(product1.getProductId());
		assertThat(item1.getQuantity()).isEqualTo(cart1.getQuantity());

		GetCartItemResponse item2 = result.get(1);
		assertThat(item2.getProductId()).isEqualTo(product2.getProductId());
		assertThat(item2.getQuantity()).isEqualTo(cart2.getQuantity());
	}

	@Test
	void getCartItems_장바구니없음() {
		User user = User.builder().userId(1L).name("테스트 유저").email("abc@gamil.com")
			.password("123").phoneNumber("000").roleCode(0).build();
		given(cartRepository.findByUser_UserId(user.getUserId())).willReturn(Collections.emptyList());

		List<GetCartItemResponse> result = cartService.getCartItems(user.getUserId());
		assertThat(result).isEmpty();
	}

}