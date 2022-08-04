package com.chinjja.talk.security;

import com.chinjja.talk.domain.user.model.User;

public class MockUser {
	public static final User user = User.builder()
			.id(1L)
			.username("user")
			.password("1234")
			.build();
}
