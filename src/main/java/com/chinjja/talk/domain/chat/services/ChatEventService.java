package com.chinjja.talk.domain.chat.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import com.chinjja.talk.domain.chat.event.ChatEvent;
import com.chinjja.talk.domain.chat.event.ChatMessageEvent;
import com.chinjja.talk.domain.chat.event.ChatUserEvent;
import com.chinjja.talk.domain.messenger.services.MessengerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatEventService {
	private final MessengerService messengerService;

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
}
