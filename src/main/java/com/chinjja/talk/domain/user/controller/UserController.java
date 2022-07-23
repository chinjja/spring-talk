package com.chinjja.talk.domain.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chinjja.talk.domain.user.converter.UserToUserDtoConverter;
import com.chinjja.talk.domain.user.dto.UserDto;
import com.chinjja.talk.domain.user.model.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
	private final UserToUserDtoConverter userToUserDtoConverter;
	
	@GetMapping("/{username}")
	public UserDto getUserByUsername(@PathVariable("username") User user) {
		return userToUserDtoConverter.convert(user);
	}
}
