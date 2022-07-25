package com.chinjja.talk.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.chinjja.talk.domain.auth.dao.ResetPasswordRepository;
import com.chinjja.talk.domain.auth.model.ResetPassword;
import com.chinjja.talk.domain.auth.services.ResetPasswordService;
import com.chinjja.talk.domain.event.event.ResetPasswordSent;
import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.domain.user.services.UserService;
import com.chinjja.talk.domain.utils.CurrentContextPathProvider;
import com.chinjja.talk.domain.utils.TimeProvider;
import com.chinjja.talk.domain.utils.UuidProvider;

@ExtendWith(MockitoExtension.class)
public class ResetPasswordServiceTests {
	@InjectMocks
	ResetPasswordService resetPasswordService;
	
	@Mock CurrentContextPathProvider currentContextPathProvider;
	@Mock TimeProvider timeProvider;
	@Mock UuidProvider uuidProvider;
	@Mock UserService userService;
	@Mock PasswordEncoder passwordEncoder;
	@Mock ResetPasswordRepository resetPasswordRepository;
	@Mock ApplicationEventPublisher applicationEventPublisher;
	
	final String email = "user@user.com";
	final User user = User.builder()
			.username(email)
			.build();

	final String host = "http://localhost";
	Instant now;
	UUID uuid;
	ResetPassword resetPassword;
	
	@BeforeEach
	void setUp() {
		now = Instant.now();
		uuid = UUID.randomUUID();
		resetPassword = ResetPassword.builder()
				.email(email)
				.uuid(uuid.toString())
				.issuedAt(now)
				.build();
	}
	
	@Test
	void sendEmail() {
		when(currentContextPathProvider.getCurrentContextPath()).thenReturn(host);
		when(timeProvider.now()).thenReturn(now);
		when(uuidProvider.random()).thenReturn(uuid);
		when(userService.getByUsername(email)).thenReturn(user);
		when(resetPasswordRepository.findByUuid(uuid.toString())).thenReturn(null);
		when(resetPasswordRepository.save(resetPassword)).thenReturn(resetPassword);
		
		resetPasswordService.sendEmail(email);
		
		verify(applicationEventPublisher).publishEvent(ResetPasswordSent.builder()
				.host(host)
				.email(email)
				.uuid(uuid.toString())
				.build());
		
		verify(resetPasswordRepository).save(resetPassword);
		verify(resetPasswordRepository, never()).delete(any());
		verifyNoMoreInteractions(userService, uuidProvider, resetPasswordRepository, applicationEventPublisher);
	}
	
	@Test
	void whenResetPasswordIsExists_thenDeleteAndSave() {
		when(currentContextPathProvider.getCurrentContextPath()).thenReturn(host);
		when(timeProvider.now()).thenReturn(now);
		when(uuidProvider.random()).thenReturn(uuid);
		when(userService.getByUsername(email)).thenReturn(user);
		when(resetPasswordRepository.findByUuid(uuid.toString())).thenReturn(resetPassword);
		when(resetPasswordRepository.save(resetPassword)).thenReturn(resetPassword);
		
		resetPasswordService.sendEmail(email);
		
		verify(applicationEventPublisher).publishEvent(ResetPasswordSent.builder()
				.host(host)
				.email(email)
				.uuid(uuid.toString())
				.build());
		
		verify(resetPasswordRepository).save(resetPassword);
		verify(resetPasswordRepository).delete(resetPassword);
		verifyNoMoreInteractions(userService, uuidProvider, resetPasswordRepository, applicationEventPublisher);
	}
	
	@Test
	void whenUserIsNotExists_thenThrowAnUsernameNotFound() {
		assertThrows(UsernameNotFoundException.class, () -> {
			resetPasswordService.sendEmail(email);
		});
		verifyNoInteractions(resetPasswordRepository, applicationEventPublisher);
	}
	
	@Test
	void resetPasword() {
		when(timeProvider.now()).thenReturn(now);
		when(passwordEncoder.encode("1234")).thenReturn("kkk");
		when(userService.getByUsername(email)).thenReturn(user);
		when(resetPasswordRepository.findByUuid(uuid.toString())).thenReturn(resetPassword);
		
		resetPasswordService.reset(uuid.toString(), "1234");
		
		verify(userService).save(User.builder()
				.username(email)
				.password("kkk")
				.build());
		verify(resetPasswordRepository).delete(resetPassword);
		verifyNoMoreInteractions(userService, passwordEncoder, resetPasswordRepository);
		verifyNoInteractions(uuidProvider, applicationEventPublisher);
	}
	
	@Test
	void whenUserIsNotExists_thenTheResetThrowAnUsernameNotFound() {
		var resetPassword = ResetPassword.builder()
				.email(email)
				.issuedAt(now)
				.build();
		when(timeProvider.now()).thenReturn(now);
		when(resetPasswordRepository.findByUuid(uuid.toString())).thenReturn(resetPassword);
		
		assertThrows(UsernameNotFoundException.class, () -> {
			resetPasswordService.reset(uuid.toString(), "1234");
		});
		
		verifyNoMoreInteractions(resetPasswordRepository);
		verifyNoInteractions(uuidProvider,applicationEventPublisher);
	}
	
	@Test
	void whenResetPasswordIsNotExists_thenThrowAnIllegalArgument() {
		when(resetPasswordRepository.findByUuid(uuid.toString())).thenReturn(null);
		var e = assertThrows(IllegalArgumentException.class, () -> {
			resetPasswordService.reset(uuid.toString(), "1234");
		});
		assertEquals("no reset password infomation", e.getMessage());
		
		verifyNoMoreInteractions(resetPasswordRepository);
		verifyNoInteractions(userService, applicationEventPublisher);
	}
	
	@Test
	void whenResetPasswordIsExpired_thenThrowAnIllegalArgument() {
		var resetPassword = ResetPassword.builder()
				.email(email)
				.issuedAt(now)
				.build();
		when(timeProvider.now()).thenReturn(now.plusSeconds(301));
		when(resetPasswordRepository.findByUuid(uuid.toString())).thenReturn(resetPassword);
		
		var e = assertThrows(IllegalArgumentException.class, () -> {
			resetPasswordService.reset(uuid.toString(), "1234");
		});
		assertEquals("the reset password is expired", e.getMessage());
		
		verifyNoMoreInteractions(resetPasswordRepository);
		verifyNoInteractions(userService, applicationEventPublisher);
	}
	
	@Test
	void isValid() {
		when(timeProvider.now()).thenReturn(now);
		when(resetPasswordRepository.findByUuid(uuid.toString())).thenReturn(resetPassword);
		
		assertTrue(resetPasswordService.isValid(uuid.toString()));
		
		verifyNoMoreInteractions(resetPasswordRepository);
		verifyNoInteractions(userService, applicationEventPublisher);
	}
	
	@Test
	void whenResetPasswordIsNotExists_thenReturnFalse() {
		when(resetPasswordRepository.findByUuid(uuid.toString())).thenReturn(null);
		
		assertFalse(resetPasswordService.isValid(uuid.toString()));
	}
}
