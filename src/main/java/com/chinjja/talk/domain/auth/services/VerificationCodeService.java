package com.chinjja.talk.domain.auth.services;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chinjja.talk.domain.auth.common.RandomProvider;
import com.chinjja.talk.domain.auth.dao.VerificationCodeRepository;
import com.chinjja.talk.domain.auth.event.VerifyCodeSent;
import com.chinjja.talk.domain.auth.model.VerificationCode;
import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.domain.user.services.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VerificationCodeService {
	private final RandomProvider randomProvider;
	private final UserService userService;
	private final VerificationCodeRepository verificationCodeRepository;
	private final ApplicationEventPublisher applicationEventPublisher;
	
	@Transactional
	public void sendCode(User auth) {
		Objects.requireNonNull(auth);
		if(isVerified(auth)) throw new IllegalArgumentException("already verified");
		
		var code = Integer.toString(randomProvider.nextInt());
		var pad = Math.max(0, 6 - code.length());
		for(int i = 0; i < pad; i++) {
			code = "0"+code;
		}
		var vc = verificationCodeRepository.findByUser(auth);
		if(vc == null) {
			vc = VerificationCode.builder()
					.user(auth)
					.build();
		}
		vc.setIssuedAt(Instant.now());
		vc.setCode(code);
		verificationCodeRepository.save(vc);
		applicationEventPublisher.publishEvent(new VerifyCodeSent(auth, code));
		log.info("send code. {}, {}", auth, code);
	}
	
	@Transactional
	public void verifyCode(User auth, String code) {
		Objects.requireNonNull(auth);
		if(isVerified(auth)) throw new IllegalArgumentException("already verified");
		
		var vc = verificationCodeRepository.findByUser(auth);
		if(vc == null) throw new IllegalStateException("no issued code");
		var now = Instant.now();
		var duration = Duration.between(vc.getIssuedAt(), now);
		if(duration.toSeconds() > 180) throw new IllegalStateException("timeout (180s)");
		if(!vc.getCode().equals(code)) throw new IllegalArgumentException("invalid code: " + code + ", " + vc.getCode());
		
		verificationCodeRepository.delete(vc);
		userService.addRole(auth, "ROLE_USER");
		log.info("verify code. {}, {}", auth, code);
	}
	
	public boolean isVerified(User auth) {
		return !auth.getAuthorities().isEmpty();
	}
}
