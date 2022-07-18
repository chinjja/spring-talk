package com.chinjja.talk.repository;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import javax.validation.ValidationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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
				.username("user@gmail.com")
				.password("1234")
				.build());
		entityManager.flush();
		
		assertNotNull(data.getId());
		assertEquals("user@gmail.com", data.getUsername());
		assertEquals("1234", data.getPassword());
		assertTrue(data.getAuthorities().isEmpty());
	}
	
	@Test
	void saveWithRole() {
		var data = userRepository.save(User.builder()
				.username("user@gmail.com")
				.password("1234")
				.role("ROLE_USER")
				.build());
		entityManager.flush();
		
		assertNotNull(data.getId());
		assertEquals("user@gmail.com", data.getUsername());
		assertEquals("1234", data.getPassword());
		assertEquals(1, data.getAuthorities().size());
		assertArrayEquals(Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")).toArray(), data.getAuthorities().toArray());
	}
	
	@Test
	void whenUsernameIsNotEmail_thenShouldFail() {
		assertThrows(ValidationException.class, () -> {
			userRepository.save(User.builder()
					.username("user")
					.password("1234")
					.build());
			entityManager.flush();
		});
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
					.username("user@gmail.com")
					.password("1234")
					.build());
			entityManager.flush();
			entityManager.clear();
		}
		
		@Test
		void addRole() {
			var roles = new HashSet<>(user.getRoles());
			roles.add("ROLE_USER");
			user.setRoles(roles);
			userRepository.save(user);
			entityManager.flush();
			entityManager.clear();
			
			var loaded = userRepository.findById(user.getId()).get();
			assertEquals(1, loaded.getRoles().size());
		}
		
		@Test
		void duplicate_username() {
			assertThrows(Exception.class, () -> {
				userRepository.save(User.builder()
						.username("user@gmail.com")
						.password("1234")
						.build());
				entityManager.flush();
			});
		}
		
		@Test
		void exists_by_username() {
			assertTrue(userRepository.existsByUsername("user@gmail.com"));
		}
		
		@Test
		void find_by_username() {
			var actual = userRepository.findByUsername("user@gmail.com");
			assertEquals(user, actual);
		}
		
		@Test
		void exists_first() {
			assertTrue(userRepository.existsBy());
		}
	}
}
