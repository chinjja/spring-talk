package com.chinjja.talk.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.chinjja.talk.domain.user.controller.FriendController;
import com.chinjja.talk.domain.user.dto.AddFriendRequest;
import com.chinjja.talk.domain.user.model.Friend;
import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.domain.user.services.FriendService;
import com.chinjja.talk.domain.user.services.UserService;
import com.chinjja.talk.security.WithMockCustomUser;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(FriendController.class)
@AutoConfigureMockMvc(addFilters = false)
public class FriendControllerTests {
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@MockBean
	FriendService friendService;
	
	@MockBean
	UserService userService;
	
	User user;
	User other;
	Friend friend;
	
	@BeforeEach
	void setUp() {
		user = User.builder()
				.username("user")
				.password("1234")
				.build();
		
		other = User.builder()
				.username("other")
				.password("1234")
				.build();
		
		friend = Friend.builder()
				.user(user)
				.other(other)
				.build();
		
		doReturn(user).when(userService).loadUserByUsername("user");
		doReturn(other).when(userService).loadUserByUsername("other");
	}
	@Test
	@WithMockCustomUser
	void getFriends() throws Exception {
		var list = new ArrayList<User>();
		list.add(user);
		list.add(other);
		
		var responseBody = objectMapper.writeValueAsString(list);
		
		doReturn(list).when(friendService).getFriends(user);
		
		mockMvc.perform(get("/friends"))
		.andExpect(status().isOk())
		.andExpect(content().json(responseBody));
		
		verify(friendService).getFriends(user);
	}
	
	@Test
	@WithMockCustomUser
	void getFriend() throws Exception {
		var responseBody = objectMapper.writeValueAsString(other);
		
		doReturn(other).when(friendService).getFriend(user, other);
		
		mockMvc.perform(get("/friends/other"))
		.andExpect(status().isOk())
		.andExpect(content().json(responseBody));
		
		verify(friendService).getFriend(user, other);
	}

	@Test
	@WithMockCustomUser
	void addFriend() throws Exception {
		var requestDto = AddFriendRequest.builder()
				.username("other")
				.build();
		var requestBody = objectMapper.writeValueAsString(requestDto);
		var responseBody = objectMapper.writeValueAsString(other);
		
		
		doReturn(friend).when(friendService).addFriend(user, requestDto);
		
		mockMvc.perform(post("/friends")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
		.andExpect(status().isCreated())
		.andExpect(content().json(responseBody));
		
		verify(friendService).addFriend(user, requestDto);
	}

	@Test
	@WithMockCustomUser
	void removeFriend() throws Exception {
		doNothing().when(friendService).removeFriend(user, other);
		
		mockMvc.perform(delete("/friends/other"))
		.andExpect(status().isOk());
		
		verify(friendService).removeFriend(user, other);
	}
}
