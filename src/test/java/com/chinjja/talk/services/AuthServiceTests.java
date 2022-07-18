package com.chinjja.talk.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.chinjja.talk.domain.auth.common.JwtTokenProvider;
import com.chinjja.talk.domain.auth.dao.TokenRepository;
import com.chinjja.talk.domain.auth.dto.LoginRequest;
import com.chinjja.talk.domain.auth.dto.RefreshTokenRequest;
import com.chinjja.talk.domain.auth.dto.RegisterRequest;
import com.chinjja.talk.domain.auth.model.Token;
import com.chinjja.talk.domain.auth.services.AuthService;
import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.domain.user.services.UserService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {
	@Mock
	AuthenticationManager authenticationManager;
	
	@Mock
	TokenRepository tokenRepository;
	
	@Mock
	PasswordEncoder passwordEncoder;
	
	@Mock
	JwtTokenProvider jwtTokenProvider;
	
	@Mock
	UserService userService;
	
	@InjectMocks
	AuthService authService;
	
	User user;
	Token token;
	final String accessToken = "1234";
	final String refreshToken = "5678";
	
	@BeforeEach
	void setUp() {
		user = User.builder()
				.username("user")
				.password("1234")
				.build();
		
		token = Token.builder()
				.user(user)
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}
	
	@Test
	void register() {
		var encodedUser = User.builder()
				.username("username")
				.password("encoded")
				.build();
		when(passwordEncoder.encode("password")).thenReturn("encoded");
		when(userService.save(encodedUser)).thenReturn(encodedUser);
		
		var actual = authService.register(RegisterRequest.builder()
				.username("username")
				.password("password")
				.build());
		assertEquals(encodedUser, actual);
		
		verify(userService).save(encodedUser);
	}
	
	@Test
	void initAdmin() {
		when(passwordEncoder.encode("1234")).thenReturn("encoded");
		authService.initAdmin();
		verify(userService).save(User.builder()
				.username("admin@admin.com")
				.password("encoded")
				.build());
		verify(userService).addRole(any(), eq("ROLE_USER"), eq("ROLE_ADMIN"));
	}
	
	@Test
	void initAdmin2() {
		when(userService.hasAnyUser()).thenReturn(true);
		authService.initAdmin();
		verify(userService, never()).save(any());
	}
	
	@Test
	void login() {
		when(userService.getByUsername(user.getUsername())).thenReturn(user);
		when(authenticationManager.authenticate(any())).thenReturn(null);
		when(jwtTokenProvider.generateAccessToken(any())).thenReturn(accessToken);
		when(jwtTokenProvider.generateRefreshToken(any())).thenReturn(refreshToken);
		when(tokenRepository.findByUser(user)).thenReturn(token);
		when(tokenRepository.save(token)).thenReturn(token);
		
		authService.login(LoginRequest.builder()
				.username("user")
				.password("1234")
				.build());
		
		verify(tokenRepository).save(Token.builder()
				.user(user)
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build());
	}
	
	@Test
	void whenAuthenticateThrowsException_thenShouldFail() {
		when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException());
		assertThrows(Exception.class, () -> {
			authService.login(LoginRequest.builder()
					.username("user")
					.password("1234")
					.build());
		});
		verify(tokenRepository, never()).save(any());
	}
	
	@Test
	void logout() {
		authService.logout(user);
		verify(tokenRepository).deleteByUser(user);
	}
	
	@Test
	void whenUserIsNull_thenShouldSkip() {
		authService.logout(null);
		verify(tokenRepository, never()).deleteByUser(any());
	}
	
	@Test
	void refresh() {
		var newAccessToken = "newAccessToken";
		when(userService.getByUsername(user.getUsername())).thenReturn(user);
		when(tokenRepository.findByUser(user)).thenReturn(token);
		when(tokenRepository.save(any())).thenReturn(token.toBuilder().
				accessToken(newAccessToken)
				.build());
		when(jwtTokenProvider.generateAccessToken(user)).thenReturn(newAccessToken);
		when(jwtTokenProvider.validateRefreshToken(token.getRefreshToken())).thenReturn(true);
		when(jwtTokenProvider.getUsernameFromToken(token.getAccessToken())).thenReturn(user.getUsername());
		
		var dto = RefreshTokenRequest.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
		var newToken = authService.refresh(dto);
		assertEquals(newAccessToken, newToken.getAccessToken());
		assertEquals(refreshToken, newToken.getRefreshToken());
		
		verify(tokenRepository).save(Token.builder()
				.user(user)
				.accessToken(newAccessToken)
				.refreshToken(refreshToken)
				.build());
		verify(jwtTokenProvider).generateAccessToken(user);
		verify(jwtTokenProvider, never()).generateRefreshToken(any());
	}
}
