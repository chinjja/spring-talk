package com.chinjja.talk.domain.messenger.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.chinjja.talk.domain.chat.converter.ChatMessageToChatMessageDtoConverter;
import com.chinjja.talk.domain.chat.converter.ChatToChatDtoConverter;
import com.chinjja.talk.domain.chat.converter.ChatUserToChatUserDtoConverter;
import com.chinjja.talk.domain.chat.model.Chat;
import com.chinjja.talk.domain.chat.model.ChatMessage;
import com.chinjja.talk.domain.chat.model.ChatUser;
import com.chinjja.talk.domain.messenger.dto.ChatMessengerDto;
import com.chinjja.talk.domain.user.converter.UserToUserDtoConverter;
import com.chinjja.talk.domain.user.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessengerService {
	private final SimpMessagingTemplate template;
	private final ChatToChatDtoConverter toChatDtoConverter;
	private final ChatUserToChatUserDtoConverter toChatUserDtoConverter;
	private final ChatMessageToChatMessageDtoConverter toChatMessageDtoConverter;
	private final UserToUserDtoConverter toUserDtoConverter;
	
	public void toChat(String command, ChatUser payload) {
		toChat(payload.getChat(), command, "ChatUser", toChatUserDtoConverter.convert(payload));
	}
	
	public void toChat(String command, List<ChatUser> payload) {
		if(payload.isEmpty()) return;
		toChat(payload.get(0).getChat(), command, "ChatUserList", payload.stream()
				.map(toChatUserDtoConverter::convert)
				.collect(Collectors.toList()));
	}
	
	public void toChat(String command, ChatMessage payload) {
		toChat(payload.getChat(), command, "ChatMessage", toChatMessageDtoConverter.convert(payload));
	}
	
	private void toChat(Chat chat, String command, String objectType, Object payload) {
		toChat(chat.getId(), command, objectType, payload);
	}
	
	private void toChat(Long chatId, String command, String objectType, Object payload) {
		var data = ChatMessengerDto.builder()
				.chatId(chatId)
				.objectType(objectType)
				.command(command)
				.data(payload)
				.build();
		template.convertAndSend("/topic/chat/"+chatId, data);
		log.info("send to chat. {}", data);
	}
	
	public void toUser(User user, String command, Chat payload) {
		toUser(user, command, "Chat", toChatDtoConverter.convert(payload));
	}
	
	public void toUser(User user, String command, User payload) {
		toUser(user, command, "Friend", toUserDtoConverter.convert(payload));
	}
	
	private void toUser(User user, String command, String objectType, Object payload) {
		var data = ChatMessengerDto.builder()
				.chatId(0)
				.objectType(objectType)
				.command(command)
				.data(payload)
				.build();
		template.convertAndSendToUser(user.getUsername(), "/topic/changed", data);
		log.info("send to user. {}, {}", user, data);
	}	
}
