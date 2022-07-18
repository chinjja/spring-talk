package com.chinjja.talk.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.chinjja.talk.domain.auth.controller.VerificationCodeController;
import com.chinjja.talk.domain.auth.dto.VerifyCodeRequest;
import com.chinjja.talk.domain.auth.services.VerificationCodeService;
import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.domain.user.services.UserService;
import com.chinjja.talk.security.WithMockCustomUser;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(VerificationCodeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class VerificationCodeControllerTests {
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@MockBean
	UserService userService;
	
	@MockBean
	VerificationCodeService verificationCodeService;
	
	User user;
	
	@BeforeEach
	void setUp() {
		user = User.builder()
				.username("user")
				.password("1234")
				.build();
	}
	@Test
	@WithMockCustomUser
	void sendCode() throws Exception {
		mockMvc.perform(post("/verification/send-code"))
		.andExpect(status().isOk())
		.andExpect(content().string(""));
		
		verify(verificationCodeService).sendCode(user);
	}
	
	@Test
	@WithMockCustomUser
	void verifyCode() throws Exception {
		var req = VerifyCodeRequest.builder()
				.code("123123")
				.build();
		
		mockMvc.perform(post("/verification/verify-code")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
		.andExpect(status().isOk())
		.andExpect(content().string(""));
		
		verify(verificationCodeService).verifyCode(user, "123123");
	}
	
	@Test
	@WithMockCustomUser
	void isVerified() throws Exception {
		when(verificationCodeService.isVerified(user)).thenReturn(true);
		
		mockMvc.perform(get("/verification/is-verified"))
		.andExpect(status().isOk())
		.andExpect(content().string(objectMapper.writeValueAsString(true)));
		
		verify(verificationCodeService).isVerified(user);
	}
	
	@Test
	@WithMockCustomUser
	void isNotVerified() throws Exception {
		when(verificationCodeService.isVerified(user)).thenReturn(false);
		
		mockMvc.perform(get("/verification/is-verified"))
		.andExpect(status().isOk())
		.andExpect(content().string(objectMapper.writeValueAsString(false)));
		
		verify(verificationCodeService).isVerified(user);
	}
}
