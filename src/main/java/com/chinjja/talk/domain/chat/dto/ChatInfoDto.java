package com.chinjja.talk.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ChatInfoDto {
	private int unreadCount;
	private int userCount;
	private ChatMessageDto latestMessage;
}
