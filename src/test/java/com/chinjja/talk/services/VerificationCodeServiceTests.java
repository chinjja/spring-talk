package com.chinjja.talk.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.chinjja.talk.domain.auth.common.RandomProvider;
import com.chinjja.talk.domain.auth.dao.VerificationCodeRepository;
import com.chinjja.talk.domain.auth.model.VerificationCode;
import com.chinjja.talk.domain.auth.services.VerificationCodeService;
import com.chinjja.talk.domain.event.event.VerifyCodeSent;
import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.domain.user.services.UserService;

@ExtendWith(MockitoExtension.class)
public class VerificationCodeServiceTests {
	@Mock
	ApplicationEventPublisher applicationEventPublisher;
	
	@Mock
	UserService userService;
	
	@Mock
	RandomProvider randomProvider;
	
	@Mock
	VerificationCodeRepository verificationCodeRepository;
	
	@InjectMocks
	VerificationCodeService verificationCodeService;
	
	@Test
	void whenVerificationCodeIsNotExists_thenSuccess() {
		var user = User.builder()
				.username("user")
				.password("1234")
				.build();
		when(randomProvider.nextInt()).thenReturn(123123);
		
		verificationCodeService.sendCode(user);
		
		verify(applicationEventPublisher).publishEvent(new VerifyCodeSent(user, "123123"));
		verify(randomProvider).nextInt();
		verify(verificationCodeRepository).save(VerificationCode.builder()
				.user(user)
				.code("123123")
				.issuedAt(any())
				.build());
	}
	
	@Test
	void whenVerificationCodeIsExists_thenSuccess() {
		var user = User.builder()
				.username("user")
				.password("1234")
				.build();
		when(randomProvider.nextInt()).thenReturn(123123);
		when(verificationCodeRepository.findByUser(any())).thenReturn(VerificationCode.builder()
				.user(user)
				.code("111111")
				.build());
		
		verificationCodeService.sendCode(user);
		
		verify(applicationEventPublisher).publishEvent(new VerifyCodeSent(user, "123123"));
		verify(randomProvider).nextInt();
		verify(verificationCodeRepository).save(VerificationCode.builder()
				.user(user)
				.code("123123")
				.issuedAt(any())
				.build());
	}
	
	@Test
	void whenUserIsNull_thenFail() {
		assertThrows(NullPointerException.class, () -> {
			verificationCodeService.sendCode(null);
		});
		
		verify(applicationEventPublisher, never()).publishEvent(any());
		verify(randomProvider, never()).nextInt();
	}
	
	@Nested
	class Verify {
		VerificationCode code;
		Instant instant;
		User user;
		
		@BeforeEach
		void setUp() {
			instant = Instant.now();
			
			code = VerificationCode.builder()
					.code("123456")
					.issuedAt(instant)
					.build();
			
			user = User.builder()
					.username("user")
					.password("1234")
					.build();
		}
		@Test
		void verifyCode() {
			when(verificationCodeRepository.findByUser(user)).thenReturn(code);
			verificationCodeService.verifyCode(user, "123456");
			verify(userService).addRole(user, "ROLE_USER");
			verify(verificationCodeRepository).delete(code);
		}
		
		@Test
		void whenCodeIsInvalid_thenFail() {
			when(verificationCodeRepository.findByUser(user)).thenReturn(code);
			assertThrows(IllegalArgumentException.class, () -> {
				verificationCodeService.verifyCode(user, "123123");
			});
			verify(userService, never()).save(any());
			verify(verificationCodeRepository, never()).delete(any());
		}
		
		@Test
		void whenTimeIsOver_thenFail() {
			Instant instant = Instant.now().minusSeconds(181);
			var code = VerificationCode.builder()
					.code("123456")
					.issuedAt(Instant.from(instant))
					.build();

			when(verificationCodeRepository.findByUser(user)).thenReturn(code);
			
			assertThrows(IllegalStateException.class, () -> {
				verificationCodeService.verifyCode(user, "123456");
			});
			verify(userService, never()).save(any());
		}
		
		@Test
		void whenUserisNull_thenFail() {
			assertThrows(NullPointerException.class, () -> {
				verificationCodeService.verifyCode(null, "123456");
			});
			verify(userService, never()).save(any());
		}
	}
	
}
