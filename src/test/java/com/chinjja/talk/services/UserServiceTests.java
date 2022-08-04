package com.chinjja.talk.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.chinjja.talk.domain.storage.model.Storage;
import com.chinjja.talk.domain.storage.services.StorageService;
import com.chinjja.talk.domain.user.dao.FriendRepository;
import com.chinjja.talk.domain.user.dao.UserRepository;
import com.chinjja.talk.domain.user.dto.UpdateProfileRequest;
import com.chinjja.talk.domain.user.event.FriendEvent;
import com.chinjja.talk.domain.user.event.UserEvent;
import com.chinjja.talk.domain.user.model.Friend;
import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.domain.user.services.FriendService;
import com.chinjja.talk.domain.user.services.UserService;
import com.chinjja.talk.domain.utils.Event;
import com.chinjja.talk.domain.utils.UuidProvider;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {
	@Mock
	UserRepository userRepository;
	
	@Mock
	FriendRepository friendRepository;
	
	@Mock
	StorageService storageService;
	
	@Mock
	FriendService friendService;
	
	@Mock
	ApplicationEventPublisher applicationEventPublisher;
	
	@Mock
	UuidProvider uuidProvider;
	
	@InjectMocks
	UserService userService;
	
	User user;
	
	@BeforeEach
	void setUp() {
		user = User.builder()
				.id(UUID.randomUUID())
				.username("username")
				.password("password")
				.build();
	}
	
	@Test
	void whenUsernameIsNotExists_thenShouldSaveTheUser() {
		when(userRepository.save(user)).thenReturn(user);
		var savedUser = userService.save(user);
		assertEquals(user, savedUser);
		
		verify(userRepository).save(user);
	}
	
	@Test
	void loadUserByUsername() {
		when(userRepository.findByUsername("username")).thenReturn(user);
		var loadedUser = userService.getByUsername("username");
		assertEquals(user, loadedUser);
	}
	
	@Test
	void whenUsernameIsNotExists_thenShouldThrowAUsernameNotFoundException() {
		when(userRepository.findByUsername("username")).thenThrow(new UsernameNotFoundException("username"));
		assertThrows(UsernameNotFoundException.class, () -> {
			userService.getByUsername("username");
		});
	}
	
	@Test
	void addRoles() {
		when(userRepository.save(user)).thenReturn(user);
		
		userService.addRole(user, "ROLE_HELLO", "ROLE_WORLD");
		
		verify(applicationEventPublisher).publishEvent(new UserEvent(Event.UPDATED, user));
		verifyNoMoreInteractions(applicationEventPublisher);
	}
	
	@Nested
	class UpdateProfile {
		UUID uuid;
		byte[] image;
		User updatedUser;
		Storage storage;
		User user1;
		User user2;
		Friend friend1;
		Friend friend2;
		
		@BeforeEach
		void setUp() {
			uuid = UUID.randomUUID();
			image = "image".getBytes();
			updatedUser = user.toBuilder()
					.name("hello")
					.state("not working")
					.photoId(uuid.toString())
					.build();
			storage = Storage.builder()
					.id(uuid.toString())
					.data(image)
					.build();

			user1 = User.builder()
					.id(UUID.randomUUID())
					.username("user1")
					.build();
			user2 = User.builder()
					.id(UUID.randomUUID())
					.username("user2")
					.build();
			friend1 = new Friend(user, user1);
			friend2 = new Friend(user, user2);
		}
		
		@Test
		void updateProfile() {
			when(storageService.save(storage)).thenReturn(storage);
			when(userRepository.save(updatedUser)).thenReturn(updatedUser);
			
			when(friendService.getFollowers(user)).thenReturn(Arrays.asList(friend1, friend2));
			when(uuidProvider.random()).thenReturn(uuid);
			
			userService.updateProfile(user, UpdateProfileRequest.builder()
					.name("hello")
					.state("not working")
					.photo(image)
					.build());
			
			verify(userRepository).save(updatedUser);
			verify(storageService).save(storage);
			
			verify(applicationEventPublisher).publishEvent(new UserEvent(Event.UPDATED, updatedUser));
			verify(applicationEventPublisher).publishEvent(new FriendEvent(Event.UPDATED, friend1));
			verify(applicationEventPublisher).publishEvent(new FriendEvent(Event.UPDATED, friend2));
			verifyNoMoreInteractions(applicationEventPublisher);
		}
		
		@Test
		void whenPhotoIdExists_thenRemoveAndInsertPhoto() {
			var removeUuid = UUID.randomUUID();
			user.setPhotoId(removeUuid.toString());
			
			when(storageService.save(storage)).thenReturn(storage);
			when(userRepository.save(updatedUser)).thenReturn(updatedUser);
			
			when(friendService.getFollowers(user)).thenReturn(Arrays.asList(friend1, friend2));
			when(uuidProvider.random()).thenReturn(uuid);
			
			userService.updateProfile(user, UpdateProfileRequest.builder()
					.name("hello")
					.state("not working")
					.photo(image)
					.build());
			verify(userRepository).save(updatedUser);
			verify(storageService).deleteById(removeUuid.toString());
			verify(storageService).save(storage);
			
			verify(applicationEventPublisher).publishEvent(new UserEvent(Event.UPDATED, updatedUser));
			verify(applicationEventPublisher).publishEvent(new FriendEvent(Event.UPDATED, friend1));
			verify(applicationEventPublisher).publishEvent(new FriendEvent(Event.UPDATED, friend2));
			verifyNoMoreInteractions(applicationEventPublisher);
		}
	}
}
