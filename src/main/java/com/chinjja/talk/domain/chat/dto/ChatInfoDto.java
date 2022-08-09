package com.chinjja.talk.domain.chat.dto;

import java.util.List;

import com.chinjja.talk.domain.user.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ChatInfoDto {
	int unreadCount;
	int userCount;
	ChatMessageDto latestMessage;
	List<User> users;
}
