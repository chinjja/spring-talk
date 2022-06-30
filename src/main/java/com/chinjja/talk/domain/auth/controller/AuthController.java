package com.chinjja.talk.domain.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.chinjja.talk.domain.auth.dto.RefreshTokenRequest;
import com.chinjja.talk.domain.auth.dto.UsernamePasswordRequest;
import com.chinjja.talk.domain.auth.exception.RefreshTokenException;
import com.chinjja.talk.domain.auth.model.Token;
import com.chinjja.talk.domain.auth.services.AuthService;
import com.chinjja.talk.domain.user.model.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {
	private final AuthService authService;
	
	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public String register(@RequestBody UsernamePasswordRequest dto) {
		var member = authService.register(dto.getUsername(), dto.getPassword());
		return member.getUsername();
	}
	
	@PostMapping("/login")
	public Token login(@RequestBody UsernamePasswordRequest dto) {
		return authService.login(dto.getUsername(), dto.getPassword());
	}
	
	@PostMapping("/logout")
	public void logout(@AuthenticationPrincipal User user) {
		authService.logout(user);
	}
	
	@PostMapping("/refresh")
	public Token refresh(@RequestBody RefreshTokenRequest dto) throws RefreshTokenException {
		return authService.refresh(dto.getAccessToken(), dto.getRefreshToken());
	}
}
