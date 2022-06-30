package com.chinjja.talk.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.chinjja.talk.converter.StringToChatConverter;
import com.chinjja.talk.domain.chat.controller.ChatMessageController;
import com.chinjja.talk.domain.chat.dto.NewMessageRequest;
import com.chinjja.talk.domain.chat.model.Chat;
import com.chinjja.talk.domain.chat.model.ChatMessage;
import com.chinjja.talk.domain.chat.services.ChatService;
import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.domain.user.services.UserService;
import com.chinjja.talk.security.WithMockCustomUser;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ChatMessageController.class)
@AutoConfigureMockMvc(addFilters = false)
@EnableJpaRepositories
public class ChatMessageControllerTests {
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
	
	User user;
	
	@BeforeEach
	void setUp() {
		user = User.builder()
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
		void sendMessage() throws Exception {
			var dto = NewMessageRequest.builder()
					.message("hello")
					.build();
			
			var message = ChatMessage.builder()
					.id(2L)
					.chat(chat)
					.sender(user)
					.message("hello")
					.build();
			
			when(chatService.sendMessage(chat, user, dto)).thenReturn(message);
			
			mockMvc.perform(post("/messages")
					.param("chatId", "1")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isCreated())
			.andExpect(content().json(objectMapper.writeValueAsString(message.getId())));
		}
		
		@Test
		void getMessages() throws Exception {
			var res = Arrays.asList(ChatMessage.builder()
					.message("hello")
					.build());
			var from = Instant.now();
			when(chatService.getMessageList(chat, user, from, 3)).thenReturn(res);
			
			mockMvc.perform(get("/messages")
					.param("chatId", "1")
					.param("limit", "3")
					.param("from", from.toString()))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(res)));
		}
		
		@Test
		void getMessageById() throws Exception {
			var message = ChatMessage.builder()
					.id(2L)
					.build();
			when(chatService.getMessage(user, 2)).thenReturn(message);
			
			mockMvc.perform(get("/messages/2"))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(message)));
		}
		
		@Test
		void getMessagesWithoutFrom() throws Exception {
			var res = Arrays.asList(ChatMessage.builder()
					.message("hello")
					.build());
			when(chatService.getMessageList(chat, user, 3)).thenReturn(res);
			
			mockMvc.perform(get("/messages")
					.param("chatId", "1")
					.param("limit", "3"))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(res)));
		}
	}
}
