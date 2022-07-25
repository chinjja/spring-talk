package com.chinjja.talk.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.chinjja.talk.domain.auth.dao.ResetPasswordRepository;
import com.chinjja.talk.domain.auth.model.ResetPassword;

@DataJpaTest
public class ResetPasswordRepositoryTests {
final String email = "user@user.coom";
	
	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	ResetPasswordRepository resetPasswordRepository;
	
	Instant now;
	String uuid;
	
	@BeforeEach
	void setUp() {
		now = Instant.now();
		uuid = UUID.randomUUID().toString();
	}
	
	@Test
	void save() {
		var resetPassword = ResetPassword.builder()
				.email(email)
				.uuid(uuid)
				.issuedAt(now.plusSeconds(1))
				.build();
		resetPassword = resetPasswordRepository.save(resetPassword);
		entityManager.flush();
		entityManager.clear();
		
		assertEquals(email, resetPassword.getEmail());
		assertEquals(uuid, resetPassword.getUuid());
		assertTrue(resetPassword.getIssuedAt().isAfter(now));
	}
	
	@Test
	void whenExists_thenSave() {
		save();
		save();
	}
	
	@Test
	void checkEmailFormatId() {
		var resetPassword = ResetPassword.builder()
				.email("user")
				.uuid(uuid)
				.issuedAt(now)
				.build();
		assertThrows(Exception.class, () -> {
			resetPasswordRepository.save(resetPassword);
			entityManager.flush();
		});
	}
	
	@Test
	void checkUuid() {
		var resetPassword = ResetPassword.builder()
				.email(email)
				.issuedAt(now)
				.build();
		assertThrows(Exception.class, () -> {
			resetPasswordRepository.save(resetPassword);
			entityManager.flush();
		});
	}
	
	@Test
	void checkIssuedAt() {
		var resetPassword = ResetPassword.builder()
				.email(email)
				.uuid(uuid)
				.build();
		assertThrows(Exception.class, () -> {
			resetPasswordRepository.save(resetPassword);
			entityManager.flush();
		});
	}
	
	@Nested
	class WithData {
		ResetPassword resetPassword;
		
		@BeforeEach
		void setUp() {
			resetPassword = resetPasswordRepository.save(ResetPassword.builder()
					.email(email)
					.uuid(uuid)
					.issuedAt(now)
					.build());
			entityManager.flush();
		}
		
		@Test
		void findByUuid() {
			var resetPassword = resetPasswordRepository.findByUuid(uuid);
			assertEquals(email, resetPassword.getEmail());
		}
		
		@Test
		void findById() {
			var resetPassword = resetPasswordRepository.findById(email);
			assertEquals(email, resetPassword.getEmail());
		}
		
		@Test
		void delete() {
			resetPasswordRepository.delete(resetPassword);
			entityManager.flush();
			entityManager.clear();
			
			assertNull(resetPasswordRepository.findById(email));
		}
	}
}
