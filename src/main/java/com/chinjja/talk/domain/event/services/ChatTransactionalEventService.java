package com.chinjja.talk.domain.event.services;

import java.util.HashMap;

import javax.mail.MessagingException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import com.chinjja.talk.domain.event.event.ChatEvent;
import com.chinjja.talk.domain.event.event.ChatMessageEvent;
import com.chinjja.talk.domain.event.event.ChatUserEvent;
import com.chinjja.talk.domain.event.event.FriendEvent;
import com.chinjja.talk.domain.event.event.ResetPasswordSent;
import com.chinjja.talk.domain.event.event.UserEvent;
import com.chinjja.talk.domain.event.event.VerifyCodeSent;
import com.chinjja.talk.domain.messenger.services.MessengerService;
import com.chinjja.talk.infra.mail.services.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatTransactionalEventService {
	private final MessengerService messengerService;
	private final EmailService emailService;

	@TransactionalEventListener
	public void onData(ChatEvent event) {
		log.info("chat: {}", event);
		messengerService.toUser(event.getUser(), event.getType(), event.getChat());
	}
	
	@TransactionalEventListener
	public void onData(ChatUserEvent event) {
		log.info("chat user: {}", event);
		messengerService.toChat(event.getType(), event.getChatUser());
	}
	
	@TransactionalEventListener
	public void onData(ChatMessageEvent event) {
		log.info("chat message: {}", event);
		messengerService.toChat(event.getType(), event.getChatMessage());
	}
	
	@TransactionalEventListener
	public void onData(FriendEvent event) {
		log.info("friend: {}", event);
		var friend = event.getFriend();
		messengerService.toUser(friend.getOwner(), event.getType(), friend);
	}
	
	@TransactionalEventListener
	public void onData(UserEvent event) {
		log.info("friend: {}", event);
		var user = event.getUser();
		messengerService.toUser(user, event.getType(), user);
	}
	
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
