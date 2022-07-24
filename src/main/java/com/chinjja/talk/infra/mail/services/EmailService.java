package com.chinjja.talk.infra.mail.services;

import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailService {
	private final JavaMailSender emailSender;
	private final SpringTemplateEngine templateEngine;
	
	public void sendSimpleMessage(String to, String subject, String text) {
		var message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		
		emailSender.send(message);
		log.info("send email to: {}, message: {}", to, message);
	}
	
	public void sendHtml(String to, String subject, String template, Map<String, String> values) throws MessagingException {
		var message = emailSender.createMimeMessage();
		var helper = new MimeMessageHelper(message, true);
		helper.setSubject(subject);
		helper.setTo(to);
		
		var context = new Context();
		for(var e : values.entrySet()) {
			context.setVariable(e.getKey(), e.getValue());
		}
		var html = templateEngine.process(template, context);
		helper.setText(html, true);
		
		emailSender.send(message);
	}
}
