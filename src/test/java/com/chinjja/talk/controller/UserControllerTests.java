package com.chinjja.talk.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.chinjja.talk.domain.user.controller.UserController;
import com.chinjja.talk.domain.user.dto.UserDto;
import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.domain.user.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTests {
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@MockBean
	UserService userService;
	
	User user;
	
	@BeforeEach
	void setUp() {
		user = User.builder()
				.username("user")
				.password("1234")
				.build();
	}
	@Test
	void getByUsername() throws Exception {
		when(userService.getByUsername("user")).thenReturn(user);
		
		var dto = UserDto.builder()
				.username(user.getUsername())
				.build();
		
		mockMvc.perform(get("/users/user"))
		.andExpect(status().isOk())
		.andExpect(content().json(objectMapper.writeValueAsString(dto)));
		
		verify(userService).getByUsername("user");
	}
}
