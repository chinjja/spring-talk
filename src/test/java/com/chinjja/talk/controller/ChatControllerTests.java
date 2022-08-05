package com.chinjja.talk.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
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
import com.chinjja.talk.domain.chat.controller.ChatController;
import com.chinjja.talk.domain.chat.dto.ChatDto;
import com.chinjja.talk.domain.chat.dto.ChatInfoDto;
import com.chinjja.talk.domain.chat.dto.ChatMessageDto;
import com.chinjja.talk.domain.chat.dto.NewDirectChatRequest;
import com.chinjja.talk.domain.chat.dto.NewGroupChatRequest;
import com.chinjja.talk.domain.chat.dto.NewOpenChatRequest;
import com.chinjja.talk.domain.chat.model.Chat;
import com.chinjja.talk.domain.chat.model.ChatMessage;
import com.chinjja.talk.domain.chat.services.ChatService;
import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.domain.user.services.UserService;
import com.chinjja.talk.security.MockUser;
import com.chinjja.talk.security.WithMockCustomUser;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ChatController.class)
@AutoConfigureMockMvc(addFilters = false)
@EnableJpaRepositories
public class ChatControllerTests {
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
		@Test
		void createOpenChat() throws Exception {
			var chat = Chat.builder()
					.id(1L)
					.title("chat")
					.description("desc")
					.joinable(true)
					.visible(true)
					.build();
			
			var request = NewOpenChatRequest.builder()
					.title("chat")
					.description("desc")
					.visible(true)
					.build();
			
			doReturn(chat).when(chatService).createOpenChat(user, request);
			
			mockMvc.perform(post("/chats/open")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(content().string("1"));
			
			verify(chatService).createOpenChat(user, request);
		}
		
		@Test
		void createDirectChat() throws Exception {
			var chat = Chat.builder()
					.id(1L)
					.joinable(false)
					.visible(false)
					.build();
			
			var request = NewDirectChatRequest.builder()
					.username("other")
					.build();
			
			doReturn(chat).when(chatService).createDirectChat(user, request);
			
			mockMvc.perform(post("/chats/direct")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(content().string("1"));
			
			verify(chatService).createDirectChat(user, request);
		}
		
		@Test
		void createGroupChat() throws Exception {
			var chat = Chat.builder()
					.id(1L)
					.joinable(false)
					.visible(false)
					.build();
			
			var request = NewGroupChatRequest.builder()
					.title("group")
					.build();
			
			doReturn(chat).when(chatService).createGroupChat(user, request);
			
			mockMvc.perform(post("/chats/group")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(content().string("1"));
			
			verify(chatService).createGroupChat(user, request);
		}
		
		@Test
		void getOpenChats() throws Exception {
			var chat = Chat.builder()
					.title("chat")
					.build();
			var res = Arrays.asList(chat, chat);
			var dto = res.stream()
					.map(x -> modelMapper.map(x, ChatDto.class))
					.collect(Collectors.toList());
			
			doReturn(res).when(chatService).getPublicChats();
			
			mockMvc.perform(get("/chats")
					.param("type", "open"))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(dto)));
		}
		
		@Test
		void getJoinedChats() throws Exception {
			var chat = Chat.builder()
					.title("chat")
					.build();
			var res = Arrays.asList(chat, chat);
			var dto = res.stream()
					.map(x -> modelMapper.map(x, ChatDto.class))
					.collect(Collectors.toList());
			
			doReturn(res).when(chatService).getJoinedChatList(user);
			
			mockMvc.perform(get("/chats")
					.param("type", "join"))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(dto)));
		}
		
		@Test
		void getDirectChat() throws Exception {
			var chat = Chat.builder()
					.title("chat")
					.build();
			var dto = modelMapper.map(chat, ChatDto.class);

			when(userService.getByUsername("user")).thenReturn(user);
			when(chatService.getDirectChat(user, user)).thenReturn(chat);
			
			mockMvc.perform(get("/chats/direct/user"))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(dto)));
		}
		
		@Test
		void whenDirectChatNotExists_thenReturn404() throws Exception {
			when(userService.getByUsername("user")).thenReturn(user);
			when(chatService.getDirectChat(user, user)).thenReturn(null);
			
			mockMvc.perform(get("/chats/direct/user"))
			.andExpect(status().isNotFound());
		}
		
		@Test
		void deleteChat() throws Exception {
			var chat = Chat.builder()
					.build();
			doReturn(chat).when(stringToChatConverter).convert("1");
			doNothing().when(chatService).deleteChat(chat, user);
			
			mockMvc.perform(delete("/chats/1"))
			.andExpect(status().isOk());
			
			verify(chatService).deleteChat(chat, user);
		}
		
		@Test
		void getChatInfo() throws Exception {
			var chat = Chat.builder()
					.title("info")
					.build();
			
			var message = ChatMessage.builder()
					.chat(chat)
					.message("hello")
					.build();
			
			var info = ChatInfoDto.builder()
					.unreadCount(10)
					.userCount(11)
					.latestMessage(modelMapper.map(message, ChatMessageDto.class))
					.build();
			
			doReturn(chat).when(stringToChatConverter).convert("1");
			doReturn(info).when(chatService).getChatInfo(chat, user);
			
			mockMvc.perform(get("/chats/1/info"))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(info)));
		}
		
		@Test
		void read() throws Exception {
			var chat = Chat.builder()
					.title("read")
					.build();
			
			doReturn(chat).when(stringToChatConverter).convert("1");
			
			mockMvc.perform(post("/chats/1/read"))
			.andExpect(status().isOk());
			
			verify(chatService).read(chat, user);
		}
	}
}
