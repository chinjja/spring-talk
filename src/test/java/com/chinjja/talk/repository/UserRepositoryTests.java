package com.chinjja.talk.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.chinjja.talk.domain.user.dao.UserRepository;
import com.chinjja.talk.domain.user.model.User;

@DataJpaTest
class UserRepositoryTests {
	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	UserRepository userRepository;
	
	@Test
	void save() {
		var data = userRepository.save(User.builder()
				.username("user")
				.password("1234")
				.build());
		entityManager.flush();
		
		assertNotNull(data.getId());
		assertEquals("user", data.getUsername());
		assertEquals("1234", data.getPassword());
	}
	
	@Test
	void require_username() {
		assertThrows(Exception.class, () -> {
			userRepository.save(User.builder()
					.password("1234")
					.build());
			entityManager.flush();
		});
	}
	
	@Test
	void require_password() {
		assertThrows(Exception.class, () -> {
			userRepository.save(User.builder()
					.username("user")
					.build());
			entityManager.flush();
		});
	}
	
	@Test
	void exists_first() {
		assertFalse(userRepository.existsBy());
	}
	
	@Nested
	class WithData {
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
		void duplicate_username() {
			assertThrows(Exception.class, () -> {
				userRepository.save(User.builder()
						.username("user")
						.password("1234")
						.build());
				entityManager.flush();
			});
		}
		
		@Test
		void exists_by_username() {
			assertTrue(userRepository.existsByUsername("user"));
		}
		
		@Test
		void find_by_username() {
			var actual = userRepository.findByUsername("user");
			assertEquals(user, actual);
		}
		
		@Test
		void exists_first() {
			assertTrue(userRepository.existsBy());
		}
	}
}
