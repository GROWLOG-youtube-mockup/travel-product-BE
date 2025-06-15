package com.travelservice.global.sms;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.travelservice.domain.auth.service.SmsSender;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("prod") // 운영에서만 활성화할 경우
public class RealSmsSender implements SmsSender {
	@Override
	public void send(String to, String code) {
		// 실제 문자 전송 로직 구현 부분
		// CoolSMS, Twilio 등 SMS API를 사용하여 구현할 수 있습니다.
		// 예시로는 CoolSMS의 Java SDK를 사용할 수 있습니다.

		log.info("Sending SMS to {} with code: {}", to, code);
	}
}
