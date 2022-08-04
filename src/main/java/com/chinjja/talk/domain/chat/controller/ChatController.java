package com.chinjja.talk.domain.chat.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.chinjja.talk.domain.chat.converter.ChatToChatDtoConverter;
import com.chinjja.talk.domain.chat.dto.ChatDto;
import com.chinjja.talk.domain.chat.dto.ChatInfoDto;
import com.chinjja.talk.domain.chat.dto.NewDirectChatRequest;
import com.chinjja.talk.domain.chat.dto.NewGroupChatRequest;
import com.chinjja.talk.domain.chat.dto.NewOpenChatRequest;
import com.chinjja.talk.domain.chat.model.Chat;
import com.chinjja.talk.domain.chat.services.ChatService;
import com.chinjja.talk.domain.user.model.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {
	private final ChatService chatService;
	private final ChatToChatDtoConverter toChatDtoConverter;
	
	@GetMapping
	public List<ChatDto> getChats(
			@RequestParam("type") String type,
			@AuthenticationPrincipal User user) {
		List<Chat> list = null;
		switch(type) {
		case "open":
			list = chatService.getPublicChats();
			break;
		case "join":
			list = chatService.getJoinedChatList(user);
			break;
		default:
			throw new IllegalArgumentException("unknown type: " + type);
		}
		return list.stream()
				.map(toChatDtoConverter::convert)
				.collect(Collectors.toList());
	}
	
	@GetMapping("/{id}")
	public ChatDto getChat(@PathVariable("id") Chat chat) {
		return toChatDtoConverter.convert(chat);
	}
	
	@PostMapping("/open")
	@ResponseStatus(HttpStatus.CREATED)
	public String newOpenChat(
			@RequestBody NewOpenChatRequest dto,
			@AuthenticationPrincipal User user) {
		var chat = chatService.createOpenChat(user, dto);
		return chat.getId().toString();
	}
	
	@PostMapping("/group")
	@ResponseStatus(HttpStatus.CREATED)
	public String newGroupChat(
			@RequestBody NewGroupChatRequest dto,
			@AuthenticationPrincipal User user) {
		var chat = chatService.createGroupChat(user, dto);
		return chat.getId().toString();
	}
	
	@PostMapping("/direct")
	@ResponseStatus(HttpStatus.CREATED)
	public String newDirectChat(
			@RequestBody NewDirectChatRequest dto,
			@AuthenticationPrincipal User user) {
		var chat = chatService.createDirectChat(user, dto);
		return chat.getId().toString();
	}
	
	@DeleteMapping("/{id}")
	public void deleteChat(
			@PathVariable("id") Chat chat,
			@AuthenticationPrincipal User user) {
		chatService.deleteChat(chat, user);
	}
	
	@GetMapping("/{id}/info")
	public ChatInfoDto info(
			@PathVariable("id") Chat chat,
			@AuthenticationPrincipal User user) {
		return chatService.getChatInfo(chat, user);
	}
	
	@PostMapping("/{id}/read")
	public void read(
			@PathVariable("id") Chat chat,
			@AuthenticationPrincipal User user) {
		chatService.read(chat, user);
	}
}
