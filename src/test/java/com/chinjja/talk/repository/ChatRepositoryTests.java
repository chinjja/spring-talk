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

import com.chinjja.talk.domain.chat.dao.ChatRepository;
import com.chinjja.talk.domain.chat.model.Chat;
import com.chinjja.talk.domain.user.dao.UserRepository;
import com.chinjja.talk.domain.user.model.User;

@DataJpaTest
class ChatRepositoryTests {
	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	ChatRepository chatRepository;
	
	@Autowired
	UserRepository memberRepository;
	
	User owner;
	User user;
	
	@BeforeEach
	void setUp() {
		owner = memberRepository.save(User.builder()
				.username("owner@gmail.com")
				.password("1234")
				.build());
		user = memberRepository.save(User.builder()
				.username("user@gmail.com")
				.password("1234")
				.build());
		entityManager.flush();
	}
	
	@Test
	void save() {
		var data = chatRepository.save(Chat.builder()
				.joinable(true)
				.visible(true)
				.owner(owner)
				.title("chat1")
				.description("description")
				.build());
		entityManager.flush();

		assertNotNull(data.getId());
		assertNotNull(data.getCreatedAt());
		assertEquals(owner, data.getOwner());
		assertEquals("chat1", data.getTitle());
		assertEquals("description", data.getDescription());
		assertTrue(data.isJoinable());
		assertTrue(data.isVisible());
	}
	
	@Nested
	class WithData {
		Chat ownerChat;
		Chat userChat1;
		Chat userChat2;
		
		@BeforeEach
		void setUp() {
			ownerChat = chatRepository.save(Chat.builder()
					.owner(owner)
					.visible(true)
					.title("owner chat")
					.build());

			userChat1 = chatRepository.save(Chat.builder()
					.owner(user)
					.visible(true)
					.title("user chat1")
					.build());

			userChat2 = chatRepository.save(Chat.builder()
					.owner(user)
					.visible(false)
					.title("user chat2")
					.build());
			
			entityManager.flush();
			entityManager.clear();
		}
		
		@Test
		void find_openchat() {
			var data = chatRepository.findByVisible(true).stream()
					.map(x -> x.getId())
					.collect(Collectors.toList());
			assertThat(data).containsExactlyInAnyOrder(ownerChat.getId(), userChat1.getId());
		}
		
		@Test
		void find_by_id() {
			var data = chatRepository.findById(userChat2.getId());
			assertEquals(userChat2.getId(), data.getId());
		}
	}
}
