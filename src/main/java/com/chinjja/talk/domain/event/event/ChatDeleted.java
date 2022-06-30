package com.chinjja.talk.domain.event.event;

import com.chinjja.talk.domain.chat.model.Chat;
import com.chinjja.talk.domain.user.model.User;

import lombok.Value;

@Value
public class ChatDeleted {
	User user;
	Chat chat;
}
