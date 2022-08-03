package com.chinjja.talk.domain.chat.event;

import com.chinjja.talk.domain.chat.model.Chat;
import com.chinjja.talk.domain.user.model.User;

import lombok.Value;

@Value
public class ChatEvent {
	String type;
	User user;
	Chat chat;
}
