package com.travelservice.domain.auth.service;

public interface MailSender {
	void send(String to, String code);
}
