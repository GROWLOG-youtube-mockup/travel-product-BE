package com.travelservice.domain.auth.service;

public interface SmsSender {
	void send(String to, String code);
}
