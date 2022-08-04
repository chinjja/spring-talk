package com.chinjja.talk.domain.chat.controller;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.chinjja.talk.domain.chat.converter.ChatMessageToChatMessageDtoConverter;
import com.chinjja.talk.domain.chat.dto.ChatMessageDto;
import com.chinjja.talk.domain.chat.dto.NewMessageRequest;
import com.chinjja.talk.domain.chat.model.Chat;
import com.chinjja.talk.domain.chat.model.ChatMessage;
import com.chinjja.talk.domain.chat.services.ChatService;
import com.chinjja.talk.domain.user.model.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class ChatMessageController {
	private final ChatService chatService;
	private final ChatMessageToChatMessageDtoConverter toChatMessageDtoConverter;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UUID newMessage(
			@RequestBody NewMessageRequest dto,
			@RequestParam("chatId") Chat chat,
			@AuthenticationPrincipal User user) {
		return chatService.sendMessage(chat, user, dto).getId();
	}
	
	@GetMapping
	public List<ChatMessageDto> getMessages(
			@RequestParam("chatId") Chat chat,
			@RequestParam(name = "from", required = false) @DateTimeFormat(iso = ISO.DATE_TIME) Instant from,
			@RequestParam("limit") int limit,
			@AuthenticationPrincipal User user) {
		List<ChatMessage> list = null;
		if(from == null) {
			list = chatService.getMessageList(chat, user, limit);
		} else {
			list = chatService.getMessageList(chat, user, from, limit);
		}
		return list.stream()
				.map(toChatMessageDtoConverter::convert)
				.collect(Collectors.toList());
	}
	
	@GetMapping("/{id}")
	public ChatMessageDto getMessage(
			@PathVariable("id") UUID id,
			@AuthenticationPrincipal User user) {
		var msg = chatService.getMessage(user, id);
		return toChatMessageDtoConverter.convert(msg);
	}
}
