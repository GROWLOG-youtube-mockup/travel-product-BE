package com.travelservice.global.sms;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.travelservice.domain.auth.service.SmsSender;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("!prod") // 프로덕션 환경이 아닐 때만 활성화
public class StubSmsSender implements SmsSender { //실제 sms 서비스가 아닌, 로그로 대체하는 스텁 서비스
	@Override
	public void send(String to, String code) {
		log.info("[GrowLog - 여행서비스] stub To: {}, code: {}", to, code);
	}
}
