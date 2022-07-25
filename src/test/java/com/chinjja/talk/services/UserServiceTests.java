package com.chinjja.talk.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.chinjja.talk.domain.user.dao.FriendRepository;
import com.chinjja.talk.domain.user.dao.UserRepository;
import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.domain.user.services.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {
	@Mock
	UserRepository userRepository;
	
	@Mock
	FriendRepository friendRepository;
	
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
}
