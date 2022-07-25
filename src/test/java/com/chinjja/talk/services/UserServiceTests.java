package com.chinjja.talk.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
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
import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.domain.user.services.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {
	@Mock
	UserRepository userRepository;
	
	@Mock
	FriendRepository friendRepository;
	
	@Mock
	StorageService storageService;
	
	@Mock
	ApplicationEventPublisher applicationEventPublisher;
	
	@InjectMocks
	UserService userService;
	
	User user;
	
	@BeforeEach
	void setUp() {
		user = User.builder()
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
	void updateProfile() {
		var image = "image".getBytes();
		var updatedUser = user.toBuilder()
				.name("hello")
				.state("not working")
				.photoId(user.getUsername()+"/photo")
				.build();
		var storage = Storage.builder()
				.id(user.getUsername()+"/photo")
				.data(image)
				.build();
		
		when(storageService.save(storage)).thenReturn(storage);
		when(userRepository.save(updatedUser)).thenReturn(updatedUser);
		
		userService.updateProfile(user, UpdateProfileRequest.builder()
				.name("hello")
				.state("not working")
				.photo(image)
				.build());
		
		verify(userRepository).save(updatedUser);
	}
}
