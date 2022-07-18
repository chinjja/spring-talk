package com.chinjja.talk.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.chinjja.talk.domain.user.dao.FriendRepository;
import com.chinjja.talk.domain.user.dao.UserRepository;
import com.chinjja.talk.domain.user.model.Friend;
import com.chinjja.talk.domain.user.model.User;

@DataJpaTest
public class FriendRepositoryTests {
	@Autowired
	FriendRepository friendRepository;
	
	@Autowired
	UserRepository memberRepository;
	
	@Autowired
	TestEntityManager entityManager;
	
	User user;
	User other1;
	User other2;
	
	@BeforeEach
	void setUp() {
		user = memberRepository.save(User.builder()
				.username("user@gmail.com")
				.password("1234")
				.build());
		other1 = memberRepository.save(User.builder()
				.username("other1@gmail.com")
				.password("1234")
				.build());
		other2 = memberRepository.save(User.builder()
				.username("other2@gmail.com")
				.password("1234")
				.build());
		entityManager.flush();
		entityManager.clear();
	}
	
	@Test
	void save() {
		var saved = friendRepository.save(Friend.builder()
				.user(user)
				.other(other1)
				.build());
		
		assertNotNull(saved.getId());
		assertEquals(user, saved.getUser());
		assertEquals(other1, saved.getOther());
		entityManager.flush();
	}
	
	@Test
	void uniqueTest() {
		save();
		entityManager.clear();
		assertThrows(Exception.class, () -> {
			save();
		});
	}
	
	@Nested
	class WithData {
		Friend userOther1;
		Friend userOther2;
		Friend other1Other2;
		
		@BeforeEach
		void setUp() {
			userOther1 = friendRepository.save(Friend.builder()
					.user(user)
					.other(other1)
					.build());
			userOther2 = friendRepository.save(Friend.builder()
					.user(user)
					.other(other2)
					.build());
			other1Other2 = friendRepository.save(Friend.builder()
					.user(other1)
					.other(other2)
					.build());
		}
		
		@Test
		void findByUserAndOther() {
			var friend = friendRepository.findByUserAndOther(user, other1);
			assertEquals(userOther1, friend);
		}
		
		@Test
		void existsByUserAndOther() {
			assertTrue(friendRepository.existsByUserAndOther(user, other1));
		}
		
		@Test
		void findByUser() {
			assertThat(friendRepository.findByUser(user)).containsExactlyInAnyOrder(userOther1, userOther2);
			assertThat(friendRepository.findByUser(other1)).containsExactly(other1Other2);
			assertThat(friendRepository.findByUser(other2)).isEmpty();;
		}
		
		@Test
		void countByUser() {
			assertEquals(2, friendRepository.countByUser(user));
			assertEquals(1, friendRepository.countByUser(other1));
			assertEquals(0, friendRepository.countByUser(other2));
		}
	}
}
