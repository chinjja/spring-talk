package com.chinjja.talk.security;

import java.util.UUID;

import com.chinjja.talk.domain.user.model.User;

public class MockUser {
	public static final User user = User.builder()
			.id(UUID.fromString("2cc304d8-ee51-43f6-a595-7db7f38d889d"))
			.username("user")
			.password("1234")
			.build();
}
