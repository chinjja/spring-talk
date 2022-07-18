package com.chinjja.talk.repository;

import static org.assertj.core.api.Assertions.assertThat;
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
				.title("chat1")
				.build());
		entityManager.flush();
		entityManager.clear();
	}
	
	@Test
	void save() {
		var data = chatUserRepository.save(ChatUser.builder()
				.chat(chat)
				.user(user)
				.build());
		entityManager.flush();
		
		assertNotNull(data.getId());
		assertEquals(chat, data.getChat());
		assertEquals(user, data.getUser());
	}
	
	@Test
	void require_chat() {
		assertThrows(Exception.class, () -> {
			chatUserRepository.save(ChatUser.builder()
					.user(user)
					.build());
			entityManager.flush();
		});
	}
	
	@Test
	void require_member() {
		assertThrows(Exception.class, () -> {
			chatUserRepository.save(ChatUser.builder()
					.chat(chat)
					.build());
			entityManager.flush();
		});
	}
	
	@Test
	void findByUser() {
		var data = chatRepository.findJoinedChats(user);
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
			ownerMember = chatUserRepository.save(ChatUser.builder()
					.chat(chat)
					.user(owner)
					.build());
			userMember = chatUserRepository.save(ChatUser.builder()
					.chat(chat)
					.user(user)
					.build());
			entityManager.flush();
			entityManager.clear();
		}
		
		@Test
		void findByUser() {
			var data = chatRepository.findJoinedChats(user);
			assertThat(data).containsExactly(chat);
		}
		
		@Test
		void findByChat() {
			var data = chatUserRepository.findByChat(chat);
			assertThat(data).containsExactlyInAnyOrder(ownerMember, userMember);
		}
	}
}
