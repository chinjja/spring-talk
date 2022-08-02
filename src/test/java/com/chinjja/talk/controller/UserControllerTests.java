package com.chinjja.talk.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

import com.chinjja.talk.domain.user.controller.UserController;
import com.chinjja.talk.domain.user.dto.UpdateProfileRequest;
import com.chinjja.talk.domain.user.dto.UserDto;
import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.domain.user.services.UserService;
import com.chinjja.talk.security.WithMockCustomUser;
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
				.id(1L)
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
		
		mockMvc.perform(get("/api/users/user"))
		.andExpect(status().isOk())
		.andExpect(content().json(objectMapper.writeValueAsString(dto)));
		
		verify(userService).getByUsername("user");
	}
	
	void updateProfile(String url, User user) throws Exception {
		var image = "image".getBytes();
		var request = UpdateProfileRequest.builder()
				.name("hello")
				.state("not working")
				.photo(image)
				.build();
		
		var response = UserDto.builder()
				.username(user.getUsername())
				.name("hello")
				.state("not working")
				.photoId("user/photo")
				.build();
		
		var updatedUser = user.toBuilder()
				.name("hello")
				.state("not working")
				.photoId("user/photo")
				.build();

		when(userService.getByUsername(user.getUsername())).thenReturn(user);
		when(userService.updateProfile(user, request)).thenReturn(updatedUser);
		
		mockMvc.perform(put(url)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request)))
		.andExpect(status().isOk())
		.andExpect(content().json(objectMapper.writeValueAsString(response)));
	}
	
	@Test
	@WithMockCustomUser
	void whenUserIsAuth_thenSuccess() throws Exception {
		updateProfile("/api/users/user", user);
	}
	
	@Test
	@WithMockCustomUser
	void whenUserIsMe_thenSuccess() throws Exception {
		updateProfile("/api/users/me", user);
	}
	
	@Test
	@WithMockCustomUser
	void whenUserIsNotAuth_thenDenied() throws Exception {
		var other = user.toBuilder()
				.id(10L)
				.username("other")
				.build();
		assertThrows(Exception.class, () -> {
			updateProfile("/api/users/other", other);
		});
	}
}
