package com.chinjja.talk.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.chinjja.talk.converter.StringToChatConverter;
import com.chinjja.talk.converter.StringToChatUserConverter;
import com.chinjja.talk.domain.chat.controller.ChatUserController;
import com.chinjja.talk.domain.chat.dto.ChatUserDto;
import com.chinjja.talk.domain.chat.dto.InviteUserRequest;
import com.chinjja.talk.domain.chat.model.Chat;
import com.chinjja.talk.domain.chat.model.ChatUser;
import com.chinjja.talk.domain.chat.services.ChatService;
import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.domain.user.services.UserService;
import com.chinjja.talk.security.MockUser;
import com.chinjja.talk.security.WithMockCustomUser;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ChatUserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ChatUserControllerTests {
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
	
	@MockBean
	StringToChatUserConverter stringToChatUserConverter;
	
	User user;
	
	@BeforeEach
	void setUp() {
		user = MockUser.user;
	}
	
	@Nested
	@WithMockCustomUser
	class WithAuth {
		UUID uuid;
		Chat chat;
		
		@BeforeEach
		void setUp() {
			uuid = UUID.randomUUID();
			chat = Chat.builder()
					.id(uuid)
					.title("chat")
					.build();
			
			when(stringToChatConverter.convert(uuid.toString())).thenReturn(chat);
		}
		@Test
		void invite() throws Exception {
			var dto = InviteUserRequest.builder()
					.usernameList(Arrays.asList("a", "b"))
					.build();
			
			mockMvc.perform(post("/chat-users/invite")
					.param("chatId", uuid.toString())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isOk());
			
			verify(chatService).invite(chat, dto);
		}
		
		@Test
		void getChatUsers() throws Exception {
			var res = Arrays.asList(new ChatUser(chat, user));
			var dto = res.stream()
					.map(x -> modelMapper.map(x, ChatUserDto.class))
					.collect(Collectors.toList());
			
			when(chatService.getUserList(chat, user)).thenReturn(res);
			
			mockMvc.perform(get("/chat-users")
					.param("chatId", uuid.toString()))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(dto)));
		}
		
		@Test
		void join() throws Exception {
			var res = new ChatUser(chat, user);
			
			when(chatService.joinToChat(chat, user)).thenReturn(res);
			mockMvc.perform(post("/chat-users/join")
					.param("chatId", uuid.toString()))
			.andExpect(status().isOk());
			
			verify(chatService).joinToChat(chat, user);
		}
		
		@Test
		void leave() throws Exception {
			mockMvc.perform(post("/chat-users/leave")
					.param("chatId", uuid.toString()))
			.andExpect(status().isOk())
			.andExpect(content().string(""));
			
			verify(chatService).leaveFromChat(chat, user);
		}
	}
}
