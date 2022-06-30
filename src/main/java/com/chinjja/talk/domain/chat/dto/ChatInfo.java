package com.chinjja.talk.domain.chat.dto;

import com.chinjja.talk.domain.chat.model.ChatMessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ChatInfo {
	private int unreadCount;
	private int userCount;
	private ChatMessage latestMessage;
}
