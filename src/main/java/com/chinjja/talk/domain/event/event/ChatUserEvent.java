package com.chinjja.talk.domain.event.event;

import com.chinjja.talk.domain.chat.model.ChatUser;

import lombok.Value;

@Value
public class ChatUserEvent {
	String type;
	ChatUser chatUser;
}
