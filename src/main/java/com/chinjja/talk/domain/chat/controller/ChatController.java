package com.chinjja.talk.domain.chat.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.chinjja.talk.domain.chat.dto.ChatInfo;
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
@CrossOrigin
public class ChatController {
	private final ChatService chatService;
	
	@GetMapping
	public List<Chat> getChats(
			@RequestParam("type") String type,
			@AuthenticationPrincipal User user) {
		switch(type) {
		case "open":
			return chatService.getPublicChats();
		case "join":
			return chatService.getJoinedChatList(user);
		default:
			throw new IllegalArgumentException("unknown type: " + type);
		}
	}
	
	@GetMapping("/{id}")
	public Chat getChat(@PathVariable("id") Chat chat) {
		return chat;
	}
	
	@PostMapping("/open")
	@ResponseStatus(HttpStatus.CREATED)
	public Long newOpenChat(
			@RequestBody NewOpenChatRequest dto,
			@AuthenticationPrincipal User user) {
		var chat = chatService.createOpenChat(user, dto);
		return chat.getId();
	}
	
	@PostMapping("/group")
	@ResponseStatus(HttpStatus.CREATED)
	public Long newGroupChat(
			@RequestBody NewGroupChatRequest dto,
			@AuthenticationPrincipal User user) {
		var chat = chatService.createGroupChat(user, dto);
		return chat.getId();
	}
	
	@PostMapping("/direct")
	@ResponseStatus(HttpStatus.CREATED)
	public Long newDirectChat(
			@RequestBody NewDirectChatRequest dto,
			@AuthenticationPrincipal User user) {
		var chat = chatService.createDirectChat(user, dto);
		return chat.getId();
	}
	
	@DeleteMapping("/{id}")
	public void deleteChat(
			@PathVariable("id") Chat chat,
			@AuthenticationPrincipal User user) {
		chatService.deleteChat(chat, user);
	}
	
	@GetMapping("/{id}/info")
	public ChatInfo info(
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
