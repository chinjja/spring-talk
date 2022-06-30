package com.chinjja.talk.domain.messenger.services;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.chinjja.talk.domain.chat.model.Chat;
import com.chinjja.talk.domain.chat.model.ChatMessage;
import com.chinjja.talk.domain.chat.model.ChatUser;
import com.chinjja.talk.domain.messenger.dto.ChatMessengerDto;
import com.chinjja.talk.domain.user.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessengerService {
	private final SimpMessagingTemplate template;
	
	public void toChat(String command, ChatUser payload) {
		toChat(payload.getChat(), command, "ChatUser", payload);
	}
	
	public void toChat(String command, List<ChatUser> payload) {
		if(payload.isEmpty()) return;
		toChat(payload.get(0).getChat(), command, "ChatUserList", payload);
	}
	
	public void toChat(String command, ChatMessage payload) {
		toChat(payload.getChat(), command, "ChatMessage", payload);
	}
	
	private void toChat(Chat chat, String command, String objectType, Object payload) {
		toChat(chat.getId(), command, objectType, payload);
	}
	
	private void toChat(Long chatId, String command, String objectType, Object payload) {
		template.convertAndSend("/topic/chat/"+chatId, ChatMessengerDto.builder()
				.objectType(objectType)
				.command(command)
				.data(payload)
				.build());
	}
	
	public void toUser(User user, String command, Chat payload) {
		toUser(user, command, "Chat", payload);
	}
	
	public void toUser(User user, String command, User friend) {
		toUser(user, command, "Friend", friend);
	}
	
	private void toUser(User user, String command, String objectType, Object payload) {
		template.convertAndSendToUser(user.getUsername(), "/topic/changed", ChatMessengerDto.builder()
				.objectType(objectType)
				.command(command)
				.data(payload)
				.build());
	}	
}
