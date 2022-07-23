package com.chinjja.talk.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.chinjja.talk.converter.StringToChatConverter;
import com.chinjja.talk.converter.StringToChatUserConverter;
import com.chinjja.talk.domain.chat.controller.ChatUserController;
import com.chinjja.talk.domain.chat.dto.InviteUserRequest;
import com.chinjja.talk.domain.chat.model.Chat;
import com.chinjja.talk.domain.chat.model.ChatUser;
import com.chinjja.talk.domain.chat.services.ChatService;
import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.domain.user.services.UserService;
import com.chinjja.talk.security.WithMockCustomUser;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ChatUserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ChatUserControllerTests {
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@MockBean
	ChatService chatService;
	
	@MockBean
	UserService userService;
	
	@MockBean
	StringToChatConverter stringToChatConverter;
	
	@MockBean
	StringToChatUserConverter stringToChatUserConverter;
	
	User user;
	
	@BeforeEach
	void setUp() {
		user = User.builder()
				.id(1L)
				.username("user")
				.password("1234")
				.build();
	}
	
	@Nested
	@WithMockCustomUser
	class WithAuth {
		Chat chat;
		
		@BeforeEach
		void setUp() {
			chat = Chat.builder()
					.title("chat")
					.build();
			
			when(stringToChatConverter.convert("1")).thenReturn(chat);
		}
		@Test
		void invite() throws Exception {
			var dto = InviteUserRequest.builder()
					.usernameList(Arrays.asList("a", "b"))
					.build();
			
			var res = Arrays.asList(1L, 2L);
			
			when(chatService.invite(chat, dto)).thenReturn(res);
			
			mockMvc.perform(post("/chat-users/invite")
					.param("chatId", "1")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(res)));
			
			verify(chatService).invite(chat, dto);
		}
		
		@Test
		void getChatUsers() throws Exception {
			var res = Arrays.asList(ChatUser.builder()
					.chat(chat)
					.user(user)
					.build());
			
			when(chatService.getUserList(chat, user)).thenReturn(res);
			
			mockMvc.perform(get("/chat-users")
					.param("chatId", "1"))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(res)));
		}
		
		@Test
		void join() throws Exception {
			var res = ChatUser.builder()
					.id(2L)
					.chat(chat)
					.user(user)
					.build();
			
			when(chatService.joinToChat(chat, user)).thenReturn(res);
			mockMvc.perform(post("/chat-users/join")
					.param("chatId", "1"))
			.andExpect(status().isOk())
			.andExpect(content().json("2"));
			
			verify(chatService).joinToChat(chat, user);
		}
		
		@Test
		void leave() throws Exception {
			mockMvc.perform(post("/chat-users/leave")
					.param("chatId", "1"))
			.andExpect(status().isOk())
			.andExpect(content().string(""));
			
			verify(chatService).leaveFromChat(chat, user);
		}
	}
}
