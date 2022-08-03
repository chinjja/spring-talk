package com.chinjja.talk.domain.chat.event;

import com.chinjja.talk.domain.chat.model.ChatMessage;

import lombok.Value;

@Value
public class ChatMessageEvent {
	String type;
	ChatMessage chatMessage;
}
