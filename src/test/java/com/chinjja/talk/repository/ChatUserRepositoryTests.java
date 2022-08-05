package com.chinjja.talk.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import com.chinjja.talk.domain.chat.dao.ChatUserRepository;
import com.chinjja.talk.domain.chat.model.Chat;
import com.chinjja.talk.domain.chat.model.ChatUser;
import com.chinjja.talk.domain.user.dao.UserRepository;
import com.chinjja.talk.domain.user.model.User;

@DataJpaTest
class ChatUserRepositoryTests {
	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	ChatRepository chatRepository;
	
	@Autowired
	ChatUserRepository chatUserRepository;

	@Autowired
	UserRepository userRepository;
	
	Chat chat;
	User owner;
	User user;
	
	@BeforeEach
	void setUp() {
		owner = userRepository.save(User.builder()
				.username("owner@gmail.com")
				.password("1234")
				.build());
		user = userRepository.save(User.builder()
				.username("user@gmail.com")
				.password("1234")
				.build());
		chat = chatRepository.save(Chat.builder()
				.owner(owner)
				.type("type")
				.title("chat1")
				.build());
		entityManager.flush();
		entityManager.clear();
	}
	
	@Test
	void save() {
		var data = chatUserRepository.save(new ChatUser(chat, user));
		entityManager.flush();
		
		assertNotNull(data.getId());
		assertEquals(chat, data.getChat());
		assertEquals(user, data.getUser());
	}
	
	@Test
	void findByUser() {
		var data = chatUserRepository.findByUser(user);
		assertTrue(data.isEmpty());
	}
	
	@Test
	void findByChat() {
		var data = chatUserRepository.findByChat(chat);
		assertTrue(data.isEmpty());
	}
	
	@Test
	void existsByChatAndUser() {
		assertFalse(chatUserRepository.existsByChatAndUser(chat, owner));
		assertFalse(chatUserRepository.existsByChatAndUser(chat, user));
	}
	
	@Test
	void existsByChatOrderById() {
		assertFalse(chatUserRepository.existsByChat(chat));
		save();
		assertTrue(chatUserRepository.existsByChat(chat));
	}
	
	@Nested
	class WithValue {
		ChatUser ownerMember;
		ChatUser userMember;
		
		@BeforeEach
		void setUp() {
			ownerMember = chatUserRepository.save(new ChatUser(chat, owner));
			userMember = chatUserRepository.save(new ChatUser(chat, user));
			entityManager.flush();
			entityManager.clear();
		}
		
		@Test
		void findByUser() {
			var data = chatUserRepository.findByUser(user);
			assertEquals(1, data.size());
			assertEquals(chat.getId(), data.get(0).getChat().getId());
		}
		
		@Test
		void findByChat() {
			var data = chatUserRepository.findByChat(chat).stream()
					.map(x -> x.getId())
					.collect(Collectors.toList());
			assertThat(data).containsExactlyInAnyOrder(ownerMember.getId(), userMember.getId());
		}
	}
}
