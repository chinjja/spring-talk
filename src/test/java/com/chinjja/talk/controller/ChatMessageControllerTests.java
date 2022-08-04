package com.chinjja.talk.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.chinjja.talk.converter.StringToChatConverter;
import com.chinjja.talk.domain.chat.controller.ChatMessageController;
import com.chinjja.talk.domain.chat.dto.ChatMessageDto;
import com.chinjja.talk.domain.chat.dto.NewMessageRequest;
import com.chinjja.talk.domain.chat.model.Chat;
import com.chinjja.talk.domain.chat.model.ChatMessage;
import com.chinjja.talk.domain.chat.services.ChatService;
import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.domain.user.services.UserService;
import com.chinjja.talk.security.MockUser;
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
	
	@Autowired
	ModelMapper modelMapper;
	
	@MockBean
	ChatService chatService;
	
	@MockBean
	UserService userService;
	
	@MockBean
	StringToChatConverter stringToChatConverter;
	
	User user;
	
	@BeforeEach
	void setUp() {
		user = MockUser.user;
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
					.id(UUID.randomUUID())
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
			var dto = res.stream()
					.map(x -> modelMapper.map(x, ChatMessageDto.class))
					.collect(Collectors.toList());
			var from = Instant.now();
			when(chatService.getMessageList(chat, user, from, 3)).thenReturn(res);
			
			mockMvc.perform(get("/messages")
					.param("chatId", "1")
					.param("limit", "3")
					.param("from", from.toString()))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(dto)));
		}
		
		@Test
		void getMessageById() throws Exception {
			var uuid = UUID.randomUUID();
			var message = ChatMessage.builder()
					.id(uuid)
					.build();
			
			var res = modelMapper.map(message, ChatMessageDto.class);
			when(chatService.getMessage(user, uuid)).thenReturn(message);
			
			mockMvc.perform(get("/messages/"+uuid))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(res)));
		}
		
		@Test
		void getMessagesWithoutFrom() throws Exception {
			var res = Arrays.asList(ChatMessage.builder()
					.message("hello")
					.build());
			var dto = res.stream()
					.map(x -> modelMapper.map(x, ChatMessageDto.class))
					.collect(Collectors.toList());
			when(chatService.getMessageList(chat, user, 3)).thenReturn(res);
			
			mockMvc.perform(get("/messages")
					.param("chatId", "1")
					.param("limit", "3"))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(dto)));
		}
	}
}
