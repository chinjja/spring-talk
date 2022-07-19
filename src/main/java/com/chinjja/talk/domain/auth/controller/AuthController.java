package com.chinjja.talk.domain.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.chinjja.talk.domain.auth.dto.LoginRequest;
import com.chinjja.talk.domain.auth.dto.LoginResponse;
import com.chinjja.talk.domain.auth.dto.RefreshTokenRequest;
import com.chinjja.talk.domain.auth.dto.RegisterRequest;
import com.chinjja.talk.domain.auth.model.Token;
import com.chinjja.talk.domain.auth.services.AuthService;
import com.chinjja.talk.domain.user.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {
	private final AuthService authService;
	
	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public String register(@RequestBody RegisterRequest dto) {
		log.info("refister. {}", dto);
		var member = authService.register(dto);
		return member.getUsername();
	}
	
	@PostMapping("/login")
	public LoginResponse login(@RequestBody LoginRequest dto) {
		log.info("login. {}", dto);
		return authService.login(dto);
	}
	
	@PostMapping("/logout")
	public void logout(@AuthenticationPrincipal User user) {
		log.info("logout. {}", user);
		authService.logout(user);
	}
	
	@PostMapping("/refresh")
	public Token refresh(@RequestBody RefreshTokenRequest dto) {
		log.info("refresh token. {}", dto);
		return authService.refresh(dto);
	}
}
