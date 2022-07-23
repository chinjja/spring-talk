package com.chinjja.talk.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Collectors;

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
		var saved = friendRepository.save(new Friend(user, other1));
		
		assertNotNull(saved.getId());
		assertEquals(user, saved.getUser());
		assertEquals(other1, saved.getOther());
		entityManager.flush();
	}
	
	@Test
	void uniqueTest() {
		save();
		save();
		entityManager.clear();
		assertEquals(1, friendRepository.countByUser(user));
	}
	
	@Nested
	class WithData {
		Friend userOther1;
		Friend userOther2;
		Friend other1Other2;
		
		@BeforeEach
		void setUp() {
			userOther1 = friendRepository.save(new Friend(user, other1));
			userOther2 = friendRepository.save(new Friend(user, other2));
			other1Other2 = friendRepository.save(new Friend(other1, other2));
			entityManager.flush();
		}
		
		@Test
		void findByUserAndOther() {
			var friend = friendRepository.findByUserAndOther(user, other1);
			assertEquals(user.getUsername(), friend.getUser().getUsername());
			assertEquals(other1.getUsername(), friend.getOther().getUsername());
		}
		
		@Test
		void existsByUserAndOther() {
			assertTrue(friendRepository.existsByUserAndOther(user, other1));
		}
		
		@Test
		void findByUser() {
			var users = friendRepository.findByUser(user).stream()
					.map(x -> x.getOther().getUsername())
					.collect(Collectors.toList());
			assertEquals(2, users.size());
			assertThat(users).containsExactlyInAnyOrder(other1.getUsername(), other2.getUsername());
		}
		
		@Test
		void countByUser() {
			assertEquals(2, friendRepository.countByUser(user));
			assertEquals(1, friendRepository.countByUser(other1));
			assertEquals(0, friendRepository.countByUser(other2));
		}
	}
}
