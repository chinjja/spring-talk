package com.chinjja.talk.domain.user.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chinjja.talk.domain.user.converter.UserToUserDtoConverter;
import com.chinjja.talk.domain.user.dto.UpdateProfileRequest;
import com.chinjja.talk.domain.user.dto.UserDto;
import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.domain.user.services.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final UserToUserDtoConverter userToUserDtoConverter;
	
	@GetMapping("/{username}")
	public UserDto getUserByUsername(@PathVariable("username") User user) {
		return userToUserDtoConverter.convert(user);
	}
	
	@PutMapping("/{username}")
	@PreAuthorize("isAuthenticated() and (#auth.id == #user.id)")
	public UserDto updateProfile(
			@AuthenticationPrincipal User auth,
			@PathVariable("username") User user,
			@RequestBody UpdateProfileRequest request) {
		log.info("update profile. auth: {}, user: {}, data: {}", auth, user, request);
		user = userService.updateProfile(user, request);
		return userToUserDtoConverter.convert(user);
	}
	
	@PutMapping("/me")
	public UserDto updateProfile(
			@AuthenticationPrincipal User auth,
			@RequestBody UpdateProfileRequest request) {
		log.info("update profile by me. auth: {}, data: {}", auth, request);
		auth = userService.updateProfile(auth, request);
		return userToUserDtoConverter.convert(auth);
	}
}
