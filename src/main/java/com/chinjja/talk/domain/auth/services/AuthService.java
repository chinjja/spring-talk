package com.chinjja.talk.domain.auth.services;

import javax.transaction.Transactional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chinjja.talk.domain.auth.common.JwtTokenProvider;
import com.chinjja.talk.domain.auth.dao.TokenRepository;
import com.chinjja.talk.domain.auth.dto.LoginRequest;
import com.chinjja.talk.domain.auth.dto.LoginResponse;
import com.chinjja.talk.domain.auth.dto.RefreshTokenRequest;
import com.chinjja.talk.domain.auth.dto.RegisterRequest;
import com.chinjja.talk.domain.auth.exception.RefreshTokenException;
import com.chinjja.talk.domain.auth.model.Token;
import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.domain.user.services.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
	private final AuthenticationManager authenticationManager;
	private final TokenRepository tokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final UserService userService;


	@Transactional
	public User register(RegisterRequest dto) {
		var username = dto.getUsername();
		var password = dto.getPassword();
		var user = userService.save(User.builder()
				.username(username)
				.password(passwordEncoder.encode(password))
				.build());
		log.info("register. {}", user);
		return user;
	}
	
	@Transactional
	public void initAdmin() {
		if(!userService.hasAnyUser()) {
			var admin = register(RegisterRequest.builder()
					.username("admin@admin.com")
					.password("1234")
					.build());
			userService.addRole(admin, "ROLE_USER", "ROLE_ADMIN");
			log.info("init admin {}", admin);
		}
	}
	
	@Transactional
	public LoginResponse login(LoginRequest dto) {
		var username = dto.getUsername();
		var password = dto.getPassword();
		var auth = new UsernamePasswordAuthenticationToken(username, password);
		authenticationManager.authenticate(auth);
		
		var user = userService.getByUsername(username);
		var token = tokenRepository.findByUser(user);
		var accessToken = jwtTokenProvider.generateAccessToken(user);
		var refreshToken = jwtTokenProvider.generateRefreshToken(user);
		if(token == null) {
			token = new Token();
			token.setUser(user);
		}
		token.setAccessToken(accessToken);
		token.setRefreshToken(refreshToken);
		token = tokenRepository.save(token);
		log.info("login. {}, {}", dto, token);
		return LoginResponse.builder()
				.token(token)
				.emailVerified(!user.getAuthorities().isEmpty())
				.build();
	}
	
	@Transactional
	public void logout(User user) {
		if(user != null) {
			tokenRepository.deleteByUser(user);
			log.info("logout. {}", user);
		}
	}
	
	@Transactional
	public Token refresh(RefreshTokenRequest dto) {
		var accessToken = dto.getAccessToken();
		var refreshToken = dto.getRefreshToken();
		var username = jwtTokenProvider.getUsernameFromToken(accessToken);
		var user = userService.getByUsername(username);
		var token = tokenRepository.findByUser(user);
		if(token == null) {
			throw new RefreshTokenException();
		}
		
		if(!refreshToken.equals(token.getRefreshToken()) || !jwtTokenProvider.validateRefreshToken(refreshToken)) {
			tokenRepository.delete(token);
			throw new RefreshTokenException();
		}
		var newAccessToken = jwtTokenProvider.generateAccessToken(user);
		token.setAccessToken(newAccessToken);
		log.info("refresh token. {}, {}", user, token);
		return tokenRepository.save(token);
	}
}
