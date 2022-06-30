package com.chinjja.talk.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.chinjja.talk.domain.auth.dao.TokenRepository;
import com.chinjja.talk.domain.auth.model.Token;
import com.chinjja.talk.domain.user.dao.UserRepository;
import com.chinjja.talk.domain.user.model.User;

@DataJpaTest
public class TokenRepositoryTests {
	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	TokenRepository tokenRepository;
	
	User user;
	
	@BeforeEach
	void setUp() {
		user = userRepository.save(User.builder()
				.username("user")
				.password("1234")
				.build());
		entityManager.flush();
		entityManager.clear();
	}
	
	@Test
	void save() {
		var token = tokenRepository.save(Token.builder()
				.user(user)
				.accessToken("accessToken")
				.refreshToken("refreshToken")
				.build());
		entityManager.flush();
		entityManager.clear();
		
		assertNotNull(token.getId());
		assertEquals("accessToken", token.getAccessToken());
		assertEquals("refreshToken", token.getRefreshToken());
		assertEquals(user, token.getUser());
	}
	
	@Test
	void whenSameUserIsPassed_thenShouldFail() {
		save();
		assertThrows(Exception.class, () -> {
			save();
		});
	}
	
	@Test
	void whenNullUserIsPassed_thenShouldFail() {
		assertThrows(Exception.class, () -> {
			tokenRepository.save(Token.builder()
					.accessToken("accessToken")
					.refreshToken("refreshToken")
					.build());
			entityManager.flush();
			entityManager.clear();
		});
	}
}
