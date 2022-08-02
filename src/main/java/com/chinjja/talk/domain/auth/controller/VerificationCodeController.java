package com.chinjja.talk.domain.auth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chinjja.talk.domain.auth.dto.VerifyCodeRequest;
import com.chinjja.talk.domain.auth.services.VerificationCodeService;
import com.chinjja.talk.domain.user.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
public class VerificationCodeController {
	private final VerificationCodeService verificationCodeService;
	
	@PostMapping("/send-code")
	public void sendCode(@AuthenticationPrincipal User user) {
		log.info("send code. {}", user);
		verificationCodeService.sendCode(user);
	}
	
	@PostMapping("/verify-code")
	public void verifyCode(
			@AuthenticationPrincipal User user,
			@RequestBody VerifyCodeRequest dto) {
		log.info("verify code. {}, {}", user, dto);
		verificationCodeService.verifyCode(user, dto.getCode());
	}
	
	@GetMapping("/is-verified")
	public boolean isVerified(@AuthenticationPrincipal User user) {
		return verificationCodeService.isVerified(user);
	}
}
