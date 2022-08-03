package com.chinjja.talk.domain.auth.services;

import java.util.HashMap;

import javax.mail.MessagingException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import com.chinjja.talk.domain.auth.event.ResetPasswordSent;
import com.chinjja.talk.domain.auth.event.VerifyCodeSent;
import com.chinjja.talk.infra.mail.services.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthEventService {
	private final EmailService emailService;

	@TransactionalEventListener
	public void onData(VerifyCodeSent event) throws MessagingException {
		log.info("verification code snet. {}", event);
		var values = new HashMap<String, String>();
		values.put("subject", "Verification Code");
		values.put("code", event.getCode());
		emailService.sendHtml(event.getTo().getUsername(), "Verification Code", "verification-email/email", values);
	}
	
	@TransactionalEventListener
	public void onData(ResetPasswordSent event) throws MessagingException {
		log.info("reset password sent. {}", event);
		var values = new HashMap<String, String>();
		values.put("subject", "Reset Password");
		values.put("uuid", event.getUuid());
		values.put("host", event.getHost());
		emailService.sendHtml(event.getEmail(), "Reset Password", "reset-password/email", values);
	}
}
