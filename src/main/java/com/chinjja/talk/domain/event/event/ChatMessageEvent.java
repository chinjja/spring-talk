package com.chinjja.talk.domain.event.event;

import com.chinjja.talk.domain.chat.model.ChatMessage;

import lombok.Value;

@Value
public class ChatMessageEvent {
	String type;
	ChatMessage chatMessage;
}
