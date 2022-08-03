package com.chinjja.talk.domain.user.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import com.chinjja.talk.domain.messenger.services.MessengerService;
import com.chinjja.talk.domain.user.event.FriendEvent;
import com.chinjja.talk.domain.user.event.UserEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventService {
	private final MessengerService messengerService;

	@TransactionalEventListener
	public void onData(FriendEvent event) {
		log.info("friend: {}", event);
		var friend = event.getFriend();
		messengerService.toUser(friend.getOwner(), event.getType(), friend);
	}
	
	@TransactionalEventListener
	public void onData(UserEvent event) {
		log.info("user: {}", event);
		var user = event.getUser();
		messengerService.toUser(user, event.getType(), user);
	}
}
