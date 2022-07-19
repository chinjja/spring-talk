package com.chinjja.talk.domain.event.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import com.chinjja.talk.domain.event.event.ChatAdded;
import com.chinjja.talk.domain.event.event.ChatDeleted;
import com.chinjja.talk.domain.event.event.ChatMessageAdded;
import com.chinjja.talk.domain.event.event.ChatUserAdded;
import com.chinjja.talk.domain.event.event.ChatUserDeleted;
import com.chinjja.talk.domain.event.event.ChatUserUpdated;
import com.chinjja.talk.domain.event.event.FriendAdded;
import com.chinjja.talk.domain.event.event.FriendDeleted;
import com.chinjja.talk.domain.messenger.services.MessengerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatTransactionalEventService {
	private final MessengerService messengerService;

	@TransactionalEventListener
	public void onData(ChatAdded event) {
		log.info("chat added: {}", event.getChat());
		messengerService.toUser(event.getUser(), "added", event.getChat());
	}
	
	@TransactionalEventListener
	public void onData(ChatDeleted event) {
		log.info("chat deleted: {}", event.getChat());
		messengerService.toUser(event.getUser(), "removed", event.getChat());
	}
	
	@TransactionalEventListener
	public void onData(ChatUserAdded event) {
		log.info("chat user added: {}", event.getChatUser());
		messengerService.toChat("added", event.getChatUser());
	}
	
	@TransactionalEventListener
	public void onData(ChatUserUpdated event) {
		log.info("chat user updated: {}", event.getChatUser());
		messengerService.toChat("updated", event.getChatUser());
	}
	
	@TransactionalEventListener
	public void onData(ChatUserDeleted event) {
		log.info("chat user deleted: {}", event.getChatUser());
		messengerService.toChat("removed", event.getChatUser());
	}
	
	@TransactionalEventListener
	public void onData(ChatMessageAdded event) {
		log.info("chat message added: {}", event.getChatMessage());
		messengerService.toChat("added", event.getChatMessage());
	}
	
	@TransactionalEventListener
	public void onData(FriendAdded event) {
		log.info("friend added: {}", event.getFriend());
		messengerService.toUser(event.getUser(), "added", event.getFriend());
	}
	
	@TransactionalEventListener
	public void onData(FriendDeleted event) {
		log.info("friend deleted: {}", event.getUser());
		messengerService.toUser(event.getUser(), "removed", event.getFriend());
	}
}
