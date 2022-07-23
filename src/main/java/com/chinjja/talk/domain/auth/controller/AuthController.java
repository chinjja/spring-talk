package com.chinjja.talk.domain.auth.controller;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.chinjja.talk.domain.auth.dto.LoginRequest;
import com.chinjja.talk.domain.auth.dto.RefreshTokenRequest;
import com.chinjja.talk.domain.auth.dto.RegisterRequest;
import com.chinjja.talk.domain.auth.dto.TokenDto;
import com.chinjja.talk.domain.auth.services.AuthService;
import com.chinjja.talk.domain.user.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;
	private final ModelMapper modelMapper;
	
	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public String register(@RequestBody RegisterRequest dto) {
		log.info("refister. {}", dto);
		var member = authService.register(dto);
		return member.getUsername();
	}
	
	@PostMapping("/login")
	public TokenDto login(@RequestBody LoginRequest dto) {
		log.info("login. {}", dto);
		var token = authService.login(dto);
		return modelMapper.map(token, TokenDto.class);
	}
	
	@PostMapping("/logout")
	public void logout(@AuthenticationPrincipal User user) {
		log.info("logout. {}", user);
		authService.logout(user);
	}
	
	@PostMapping("/refresh")
	public TokenDto refresh(@RequestBody RefreshTokenRequest dto) {
		log.info("refresh token. {}", dto);
		var token = authService.refresh(dto);
		return modelMapper.map(token, TokenDto.class);
	}
}
