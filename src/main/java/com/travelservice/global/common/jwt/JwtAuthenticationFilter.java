package com.travelservice.global.common.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.travelservice.domain.user.entity.User;
import com.travelservice.domain.user.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final UserRepository userRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String token = resolveToken(request);

		if (token != null && jwtTokenProvider.validateToken(token)) {
			Long userId = jwtTokenProvider.getUserId(token);
			int roleCode = jwtTokenProvider.getRoleCode(token); // 1,2 등 꺼냄
			String authority = jwtTokenProvider.mapRoleCodeToAuthority(roleCode); // "ADMIN" 등 변환

			// ✅ DB에서 User 조회
			User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

			//develop에서 가져온 것.
			CustomUserDetails userDetails = new CustomUserDetails(userId, authority);

			UsernamePasswordAuthenticationToken authenticationToken =
				new UsernamePasswordAuthenticationToken(
					userDetails,
					null,
					userDetails.getAuthorities()
				);

			/*
			// ✅ 최소 권한 부여
			List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

			// ✅ Authentication 객체 생성
			UsernamePasswordAuthenticationToken authenticationToken =
				new UsernamePasswordAuthenticationToken(user, null, authorities);*/

			/*UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId,
				null, null);*/

			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		}
		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}
