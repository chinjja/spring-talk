package com.chinjja.talk.domain.chat.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chinjja.talk.domain.chat.dto.InviteUserRequest;
import com.chinjja.talk.domain.chat.model.Chat;
import com.chinjja.talk.domain.chat.model.ChatUser;
import com.chinjja.talk.domain.chat.services.ChatService;
import com.chinjja.talk.domain.user.model.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chat-users")
@RequiredArgsConstructor
public class ChatUserController {
	private final ChatService chatService;
	
	@PostMapping("/invite")
	public List<Long> invite(
			@RequestParam("chatId") Chat chat,
			@RequestBody InviteUserRequest dto) {
		return chatService.invite(chat, dto);
	}
	
	@PostMapping("/join")
	public Long join(
			@RequestParam("chatId") Chat chat,
			@AuthenticationPrincipal User user) {
		return chatService.joinToChat(chat, user).getId();
	}
	
	@PostMapping("/leave")
	public void leave(
			@RequestParam("chatId") Chat chat,
			@AuthenticationPrincipal User user) {
		chatService.leaveFromChat(chat, user);
	}
	
	@GetMapping
	public List<ChatUser> getChatUsers(
			@RequestParam("chatId") Chat chat,
			@RequestParam(value = "idList", required = false) List<Long> idList,
			@AuthenticationPrincipal User user) {
		if(idList == null) {
			return chatService.getUserList(chat, user);
		}
		return chatService.getUserList(chat, user, idList);
	}
}
