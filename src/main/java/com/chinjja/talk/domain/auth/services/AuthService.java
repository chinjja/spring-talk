package com.chinjja.talk.domain.auth.services;

import javax.transaction.Transactional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chinjja.talk.domain.auth.common.JwtTokenProvider;
import com.chinjja.talk.domain.auth.dao.TokenRepository;
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
	public User register(String username, String password) {
		log.info("register: {}", username);
		return userService.save(User.builder()
				.username(username)
				.password(passwordEncoder.encode(password))
				.build());
	}
	
	@Transactional
	public void initAdmin() {
		if(!userService.hasAnyUser()) {
			register("admin", "1234");
		}
	}
	
	@Transactional
	public Token login(String username, String password) {
		var auth = new UsernamePasswordAuthenticationToken(username, password);
		authenticationManager.authenticate(auth);
		
		var user = userService.loadUserByUsername(username);
		var token = tokenRepository.findByUser(user);
		var accessToken = jwtTokenProvider.generateAccessToken(user);
		var refreshToken = jwtTokenProvider.generateRefreshToken(user);
		if(token == null) {
			token = new Token();
			token.setUser(user);
		}
		token.setAccessToken(accessToken);
		token.setRefreshToken(refreshToken);
		return tokenRepository.save(token);
	}
	
	@Transactional
	public void logout(User user) {
		if(user != null) {
			tokenRepository.deleteByUser(user);
		}
	}
	
	@Transactional
	public Token refresh(String accessToken, String refreshToken) {
		var username = jwtTokenProvider.getUsernameFromToken(accessToken);
		var user = userService.loadUserByUsername(username);
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
		return tokenRepository.save(token);
	}
}
