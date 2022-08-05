package com.chinjja.talk.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;

import com.chinjja.talk.domain.chat.dao.ChatMessageRepository;
import com.chinjja.talk.domain.chat.dao.ChatRepository;
import com.chinjja.talk.domain.chat.dao.ChatUserRepository;
import com.chinjja.talk.domain.chat.model.Chat;
import com.chinjja.talk.domain.chat.model.ChatMessage;
import com.chinjja.talk.domain.chat.model.ChatUser;
import com.chinjja.talk.domain.user.dao.UserRepository;
import com.chinjja.talk.domain.user.model.User;

@DataJpaTest
class ChatMessageRepositoryTests {
	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	ChatMessageRepository chatMessageRepository;
	
	@Autowired
	ChatUserRepository chatMemberRepository;
	
	@Autowired
	ChatRepository chatRepository;
	
	@Autowired
	UserRepository memberRepository;
	
	Chat chat;
	User owner;
	User user;
	ChatUser ownerSender;
	ChatUser userSender;
	
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
		chat = chatRepository.save(Chat.builder()
				.owner(owner)
				.type("type")
				.title("chat1")
				.build());
		ownerSender = chatMemberRepository.save(new ChatUser(chat, owner));
		userSender = chatMemberRepository.save(new ChatUser(chat, user));
		entityManager.flush();
		entityManager.clear();
	}
	
	@Test
	void save() {
		var data = chatMessageRepository.save(ChatMessage.builder()
				.chat(chat)
				.sender(owner)
				.message("owner-greeting")
				.build());
		entityManager.flush();
		
		assertNotNull(data.getId());
		assertEquals(chat, data.getChat());
		assertEquals(owner, data.getSender());
		assertEquals("owner-greeting", data.getMessage());
		assertNotNull(data.getInstant());
	}
	
	@Test
	void lastMessage() {
		var message = chatMessageRepository.findTop1ByChatOrderByInstantDesc(chat);
		assertNull(message);
	}
	
	@Test
	void require_sender() {
		assertThrows(Exception.class, () -> {
			chatMessageRepository.save(ChatMessage.builder()
					.message("owner-greeting")
					.build());
			entityManager.flush();
		});
	}
	
	@Test
	void require_message() {
		assertThrows(Exception.class, () -> {
			chatMessageRepository.save(ChatMessage.builder()
					.chat(chat)
					.sender(owner)
					.build());
			entityManager.flush();
		});
	}

	@Nested
	class WithData {
		ChatMessage ownerMessage;
		ChatMessage userMessage;
		Instant time;
		
		@BeforeEach
		void setUp() {
			time = Instant.ofEpochMilli(1000000);
			ownerMessage = chatMessageRepository.save(ChatMessage.builder()
					.chat(chat)
					.sender(owner)
					.message("owner-greeting")
					.instant(time)
					.build());
			userMessage = chatMessageRepository.save(ChatMessage.builder()
					.chat(chat)
					.sender(user)
					.message("user-greeting")
					.instant(time.plusSeconds(1))
					.build());
			entityManager.flush();
			entityManager.clear();
		}
		
		@Test
		void findByChatTop1() {
			var actual = chatMessageRepository.findByChatAndInstantLessThanOrderByInstantDesc(chat, userMessage.getInstant(), PageRequest.ofSize(1));
			assertThat(actual).containsExactly(ownerMessage);
		}
		
		@Test
		void findByChatTop10() {
			var actual = chatMessageRepository.findByChatAndInstantLessThanOrderByInstantDesc(chat, userMessage.getInstant().plusSeconds(1), PageRequest.ofSize(10));
			assertThat(actual).containsExactlyInAnyOrder(userMessage, ownerMessage);
		}
		
		@Test
		void findByChatTop10LessThan() {
			var actual = chatMessageRepository.findByChatAndInstantLessThanOrderByInstantDesc(chat, ownerMessage.getInstant(), PageRequest.ofSize(10));
			assertThat(actual).isEmpty();
		}
	}
	
	@Nested
	class WithMessage301 {
		Instant instant;
		List<ChatMessage> messages;
		
		@BeforeEach
		void setUp() {
			messages = new ArrayList<>();
			instant = Instant.now().truncatedTo(ChronoUnit.MICROS);
			Instant time = instant;
			for(int i = 0; i < 10; i++) {
				var msg = chatMessageRepository.save(ChatMessage.builder()
						.chat(chat)
						.sender(owner)
						.message("greeting"+i)
						.instant(time)
						.build());
				time = time.plusSeconds(1);
				messages.add(msg);
			}
		}
		
		@Test
		void count5() {
			assertEquals(5, chatMessageRepository.findByChatAndInstantGreaterThanOrderByInstantDesc(chat, instant, PageRequest.ofSize(5)).size());
		}
		
		@Test
		void count10() {
			assertEquals(10, chatMessageRepository.findByChatAndInstantGreaterThanOrderByInstantDesc(chat, instant.minusSeconds(1), PageRequest.ofSize(100)).size());
		}
		
		@Test
		void whenReadAtFirst_thenShouldReturn9() {
			var count = chatMessageRepository.findByChatAndInstantGreaterThanOrderByInstantDesc(chat, instant, PageRequest.ofSize(100)).size();
			assertEquals(9, count);
		}
		
		@Test
		void unread0() {
			var count = chatMessageRepository.findByChatAndInstantGreaterThanOrderByInstantDesc(chat, instant.plusSeconds(9), PageRequest.ofSize(100)).size();
			assertEquals(0, count);
		}
		
		@Test
		void latestMessage() {
			var message = chatMessageRepository.findTop1ByChatOrderByInstantDesc(chat);
			assertEquals("greeting9", message.getMessage());
		}
	}
}
