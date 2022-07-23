package com.chinjja.talk.domain.chat.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chinjja.talk.domain.chat.converter.ChatUserToChatUserDtoConverter;
import com.chinjja.talk.domain.chat.dto.ChatUserDto;
import com.chinjja.talk.domain.chat.dto.InviteUserRequest;
import com.chinjja.talk.domain.chat.model.Chat;
import com.chinjja.talk.domain.chat.services.ChatService;
import com.chinjja.talk.domain.user.model.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chat-users")
@RequiredArgsConstructor
public class ChatUserController {
	private final ChatService chatService;
	private final ChatUserToChatUserDtoConverter toChatUserDtoConverter;
	
	@PostMapping("/invite")
	public void invite(
			@RequestParam("chatId") Chat chat,
			@RequestBody InviteUserRequest dto) {
		chatService.invite(chat, dto);
	}
	
	@PostMapping("/join")
	public void join(
			@RequestParam("chatId") Chat chat,
			@AuthenticationPrincipal User user) {
		chatService.joinToChat(chat, user);
	}
	
	@PostMapping("/leave")
	public void leave(
			@RequestParam("chatId") Chat chat,
			@AuthenticationPrincipal User user) {
		chatService.leaveFromChat(chat, user);
	}
	
	@GetMapping
	public List<ChatUserDto> getChatUsers(
			@RequestParam("chatId") Chat chat,
			@AuthenticationPrincipal User user) {
		var chatUser = chatService.getUserList(chat, user);
		return chatUser.stream()
				.map(toChatUserDtoConverter::convert)
				.collect(Collectors.toList());
	}
}
