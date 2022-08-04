package com.chinjja.talk.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.chinjja.talk.domain.auth.controller.AuthController;
import com.chinjja.talk.domain.auth.dto.LoginRequest;
import com.chinjja.talk.domain.auth.dto.RefreshTokenRequest;
import com.chinjja.talk.domain.auth.dto.RegisterRequest;
import com.chinjja.talk.domain.auth.dto.TokenDto;
import com.chinjja.talk.domain.auth.model.Token;
import com.chinjja.talk.domain.auth.services.AuthService;
import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.domain.user.services.UserService;
import com.chinjja.talk.security.MockUser;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTests {
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	ModelMapper modelMapper;

	@MockBean
	AuthService authService;
	
	@MockBean
	UserService userService;
	
	User user;
	Token token;
	
	@BeforeEach
	void setUp() {
		user = MockUser.user;
		token = Token.builder()
				.user(user)
				.accessToken("access")
				.refreshToken("refresh")
				.build();
	}

	@Test
	void register() throws Exception {
		var dto = RegisterRequest.builder()
				.username("user")
				.password("1234")
				.build();
		var requestBody = objectMapper.writeValueAsString(dto);
		
		when(authService.register(dto)).thenReturn(user);
		
		mockMvc.perform(post("/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
		.andExpect(status().isCreated())
		.andExpect(content().string("user"));
		
		verify(authService).register(dto);
	}
	
	@Test
	void login() throws Exception {
		var dto = LoginRequest.builder()
				.username("user")
				.password("1234")
				.build();
		var res = modelMapper.map(token, TokenDto.class);
		var requestBody = objectMapper.writeValueAsString(dto);
		var responseBody = objectMapper.writeValueAsString(res);
		
		when(authService.login(dto)).thenReturn(token);
		
		mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
		.andExpect(status().isOk())
		.andExpect(content().json(responseBody));
		
		verify(authService).login(dto);
	}
	
	@Test
	void logout() throws Exception {
		mockMvc.perform(post("/auth/logout"))
		.andExpect(status().isOk());
		
		verify(authService).logout(any());
	}
	
	@Test
	void refresh() throws Exception {
		var req = RefreshTokenRequest.builder()
				.accessToken(token.getAccessToken())
				.refreshToken(token.getRefreshToken())
				.build();
		var newToken = token.toBuilder().accessToken("newAccess").build();
		var res = modelMapper.map(newToken, TokenDto.class);
		
		when(authService.refresh(req)).thenReturn(newToken);
		
		mockMvc.perform(post("/auth/refresh")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(token)))
		.andExpect(status().isOk())
		.andExpect(content().string(objectMapper.writeValueAsString(res)));
		
		verify(authService).refresh(req);
	}
}
