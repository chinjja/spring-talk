package com.chinjja.talk.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.chinjja.talk.domain.chat.model.Chat;
import com.chinjja.talk.domain.event.event.ChatEvent;
import com.chinjja.talk.domain.event.event.Event;
import com.chinjja.talk.domain.event.event.FriendEvent;
import com.chinjja.talk.domain.user.dao.FriendRepository;
import com.chinjja.talk.domain.user.dao.UserRepository;
import com.chinjja.talk.domain.user.dto.AddFriendRequest;
import com.chinjja.talk.domain.user.model.Friend;
import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.domain.user.services.FriendService;

@ExtendWith(MockitoExtension.class)
public class FriendServiceTests {
	@Mock
	FriendRepository friendRepository;
	
	@Mock
	UserRepository userRepository;
	
	@Mock
	ApplicationEventPublisher applicationEventPublisher;
	
	@InjectMocks
	FriendService friendService;
	
	User user;
	User other;
	Friend friend;
	Chat chat;
	
	@BeforeEach
	void setUp() {
		user = User.builder()
				.id(1L)
				.username("user")
				.password("")
				.build();
		
		other = User.builder()
				.id(2L)
				.username("other")
				.password("")
				.build();
		
		friend = new Friend(user, other);
	}
	
	@Test
	void addFriend() {
		when(userRepository.findByUsername("other")).thenReturn(other);
		when(friendRepository.save(friend)).thenReturn(friend);
		
		friendService.addFriend(user, AddFriendRequest.builder()
				.username("other")
				.build());
		
		verify(friendRepository).save(friend);
		verify(applicationEventPublisher).publishEvent(new FriendEvent(Event.ADDED, friend));
	}
	
	@Test
	void whenUserAndOtherIsSame_thenShouldFail() {
		assertThrows(IllegalArgumentException.class, () -> {
			friendService.addFriend(user, AddFriendRequest.builder()
					.username("user")
					.build());
		});
	}
	
	@Test
	void whenUserAndOtherIsAlreadyFriend_thenShouldFail() {
		when(friendRepository.existsByOwnerAndUser(any(), any())).thenReturn(true);
		assertThrows(IllegalArgumentException.class, () -> {
			friendService.addFriend(user, AddFriendRequest.builder()
					.username("other")
					.build());
		});
		verify(friendRepository, never()).save(any());
		verify(applicationEventPublisher, never()).publishEvent(any());
		verify(applicationEventPublisher, never()).publishEvent(any());
	
	}
	
	@Test
	void removeFriend() {
		when(friendRepository.findByOwnerAndUser(any(), any())).thenReturn(friend);
		friendService.removeFriend(user, other);
		verify(friendRepository).delete(friend);
		verify(applicationEventPublisher).publishEvent(new FriendEvent(Event.REMOVED, friend));
		verify(applicationEventPublisher).publishEvent(new ChatEvent(Event.REMOVED, user, any()));
	}
	
	@Test
	void whenUserAndOtherIsNotFriend_thenShouldFail() {
		assertThrows(IllegalArgumentException.class, () -> {
			friendService.removeFriend(user, other);
		});
		verify(friendRepository, never()).delete(any());
		verify(applicationEventPublisher, never()).publishEvent(any());
		verify(applicationEventPublisher, never()).publishEvent(any());
	}
	
	@Test
	void getFriends() {
		friendService.getFriends(user);
	}
}
