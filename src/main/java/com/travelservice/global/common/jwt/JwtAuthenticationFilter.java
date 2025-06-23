package com.travelservice.global.common.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

			// ✅ DB에서 User 조회
			User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

			// ✅ 최소 권한 부여
			List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

			// ✅ Authentication 객체 생성
			UsernamePasswordAuthenticationToken authenticationToken =
				new UsernamePasswordAuthenticationToken(user, null, authorities);
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
