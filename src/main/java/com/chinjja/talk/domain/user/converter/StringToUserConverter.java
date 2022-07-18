package com.chinjja.talk.domain.user.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.domain.user.services.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StringToUserConverter implements Converter<String, User> {
	private final UserService userService;
	
	@Override
	public User convert(String source) {
		return userService.getByUsername(source);
	}
}
