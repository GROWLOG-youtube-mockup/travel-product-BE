package com.travelservice.global.mail;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.travelservice.config.EmailProperties;
import com.travelservice.domain.auth.service.MailSender;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SmtpMailSender implements MailSender { // SMTP를 이용한 이메일 발송 서비스 구현

	private final JavaMailSender mailSender;
	private final EmailProperties emailProperties;

	@Override
	public void send(String to, String code) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setFrom(emailProperties.getFrom());
		message.setSubject("[GrowLog - 여행 서비스] 이메일 인증번호 안내");
		message.setText("인증번호: " + code + "\n\n이 번호를 인증 페이지에 입력해주세요.");
		mailSender.send(message);
	}



	/*
	// 임시 비밀번호 발급 메소드
	public void sendTemporaryPassword(String to, String tempPassword) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setFrom(emailProperties.getFrom());
		message.setSubject("[GrowLog - 여행 서비스] 임시 비밀번호 발급");
		message.setText("임시 비밀번호: " + tempPassword + "\n\n로그인 후 반드시 비밀번호를 변경해주세요.");
		mailSender.send(message);
	}*/
}
