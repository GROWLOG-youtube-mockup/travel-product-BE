package com.travelservice.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "이메일 전송 요청 DTO")
public class EmailSendRequest {
	@Schema(description = "이메일", example = "hong@example.com")
	private String email;
}
