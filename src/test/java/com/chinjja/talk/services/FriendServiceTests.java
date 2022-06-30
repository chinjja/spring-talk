package com.chinjja.talk.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
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
import com.chinjja.talk.domain.event.event.ChatDeleted;
import com.chinjja.talk.domain.event.event.FriendAdded;
import com.chinjja.talk.domain.event.event.FriendDeleted;
import com.chinjja.talk.domain.user.dao.FriendRepository;
import com.chinjja.talk.domain.user.dto.AddFriendRequest;
import com.chinjja.talk.domain.user.model.Friend;
import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.domain.user.services.FriendService;
import com.chinjja.talk.domain.user.services.UserService;

@ExtendWith(MockitoExtension.class)
public class FriendServiceTests {
	@Mock
	FriendRepository friendRepository;
	
	@Mock
	UserService userService;
	
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
				.username("user")
				.password("")
				.build();
		
		other = User.builder()
				.username("other")
				.password("")
				.build();
		
		friend = Friend.builder()
				.user(user)
				.other(other)
				.build();
	}
	
	@Test
	void addFriend() {
		var friend = Friend.builder()
				.user(user)
				.other(other)
				.build();
		doReturn(other).when(userService).loadUserByUsername("other");
		when(friendRepository.save(friend)).thenReturn(friend);
		
		friendService.addFriend(user, AddFriendRequest.builder()
				.username("other")
				.build());
		
		verify(friendRepository).save(friend);
		verify(applicationEventPublisher).publishEvent(new FriendAdded(user, other));
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
		when(friendRepository.existsByUserAndOther(any(), any())).thenReturn(true);
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
		when(friendRepository.findByUserAndOther(any(), any())).thenReturn(friend);
		friendService.removeFriend(user, other);
		verify(friendRepository).delete(friend);
		verify(applicationEventPublisher).publishEvent(new FriendDeleted(user, other));
		verify(applicationEventPublisher).publishEvent(new ChatDeleted(user, any()));
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
