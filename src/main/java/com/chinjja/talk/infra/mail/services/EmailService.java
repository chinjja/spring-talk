package com.chinjja.talk.infra.mail.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.infra.mail.dto.TransactionalMailMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailService {
	private final JavaMailSender emailSender;
	
	public void sendSimpleMessage(User to, String subject, String text) {
		var message = new SimpleMailMessage();
		message.setTo(to.getUsername());
		message.setSubject(subject);
		message.setText(text);
		
		emailSender.send(message);
		log.info("send email to: {}, message: {}", to, message);
	}
	
	@TransactionalEventListener
	public void sendSimpleMessage(TransactionalMailMessage message) {
		sendSimpleMessage(message.getTo(), message.getSubject(), message.getText());
	}
}
