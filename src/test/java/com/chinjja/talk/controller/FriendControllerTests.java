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
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.chinjja.talk.domain.user.controller.FriendController;
import com.chinjja.talk.domain.user.dto.AddFriendRequest;
import com.chinjja.talk.domain.user.dto.FriendDto;
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
	
	@Autowired
	ModelMapper modelMapper;
	
	@MockBean
	FriendService friendService;
	
	@MockBean
	UserService userService;
	
	User user;
	User other1;
	User other2;
	Friend friend1;
	Friend friend2;
	
	@BeforeEach
	void setUp() {
		user = User.builder()
				.id(1L)
				.username("user")
				.password("1234")
				.build();
		
		other1 = User.builder()
				.id(2L)
				.username("other1")
				.password("1234")
				.build();
		
		other2 = User.builder()
				.id(2L)
				.username("other2")
				.password("1234")
				.build();
		
		friend1 = new Friend(user, other1);
		friend2 = new Friend(user, other2);
		
		doReturn(user).when(userService).getByUsername("user");
		doReturn(other1).when(userService).getByUsername("other1");
		doReturn(other2).when(userService).getByUsername("other2");
	}
	@Test
	@WithMockCustomUser
	void getFriends() throws Exception {
		var list = new ArrayList<Friend>();
		list.add(friend1);
		list.add(friend2);
		
		var dto = list.stream()
				.map(x -> modelMapper.map(x, FriendDto.class))
				.collect(Collectors.toList());
		
		doReturn(list).when(friendService).getFriends(user);
		
		mockMvc.perform(get("/api/friends"))
		.andExpect(status().isOk())
		.andExpect(content().json(objectMapper.writeValueAsString(dto)));
		
		verify(friendService).getFriends(user);
	}
	
	@Test
	@WithMockCustomUser
	void getFriend() throws Exception {
		var dto = modelMapper.map(friend1, FriendDto.class);
		
		doReturn(friend1).when(friendService).getFriend(user, other1);
		
		mockMvc.perform(get("/api/friends/other1"))
		.andExpect(status().isOk())
		.andExpect(content().json(objectMapper.writeValueAsString(dto)));
		
		verify(friendService).getFriend(user, other1);
	}

	@Test
	@WithMockCustomUser
	void addFriend() throws Exception {
		var requestDto = AddFriendRequest.builder()
				.username("other")
				.build();
		var responseDto = modelMapper.map(friend1, FriendDto.class);
		
		doReturn(friend1).when(friendService).addFriend(user, requestDto);
		
		mockMvc.perform(post("/api/friends")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
		.andExpect(status().isCreated())
		.andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
		
		verify(friendService).addFriend(user, requestDto);
	}

	@Test
	@WithMockCustomUser
	void removeFriend() throws Exception {
		doNothing().when(friendService).removeFriend(user, other1);
		
		mockMvc.perform(delete("/api/friends/other1"))
		.andExpect(status().isOk());
		
		verify(friendService).removeFriend(user, other1);
	}
}
