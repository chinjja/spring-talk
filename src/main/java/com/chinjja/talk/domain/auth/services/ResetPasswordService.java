package com.chinjja.talk.domain.auth.services;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chinjja.talk.domain.auth.dao.ResetPasswordRepository;
import com.chinjja.talk.domain.auth.model.ResetPassword;
import com.chinjja.talk.domain.event.event.ResetPasswordSent;
import com.chinjja.talk.domain.user.services.UserService;
import com.chinjja.talk.domain.utils.CurrentContextPathProvider;
import com.chinjja.talk.domain.utils.TimeProvider;
import com.chinjja.talk.domain.utils.UuidProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResetPasswordService {
	private final CurrentContextPathProvider currentContextPathProvider;
	private final TimeProvider timeProvider;
	private final UuidProvider uuidProvider;
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	private final ResetPasswordRepository resetPasswordRepository;
	private final ApplicationEventPublisher applicationEventPublisher;
	
	@Transactional
	public void sendEmail(String email) {
		var user = userService.getByUsername(email);
		if(user == null) throw new UsernameNotFoundException(email);
		
		var host = currentContextPathProvider.getCurrentContextPath();
		if(host == null) {
			log.error("no current context path");
			throw new IllegalStateException("no current context path");
		}
		var uuid = uuidProvider.random().toString();
		
		var entity = resetPasswordRepository.findByUuid(uuid);
		if(entity != null) {
			resetPasswordRepository.delete(entity);
		}
		entity = ResetPassword.builder()
				.email(email)
				.uuid(uuid)
				.issuedAt(timeProvider.now())
				.build();
		resetPasswordRepository.save(entity);
		applicationEventPublisher.publishEvent(ResetPasswordSent.builder()
				.host(host)
				.email(email)
				.uuid(uuid)
				.build());
		log.info("send email: {}", email);
	}
	
	@Transactional
	public void reset(String uuid, String password) {
		var reset = resetPasswordRepository.findByUuid(uuid);
		if(reset == null) throw new IllegalArgumentException("no reset password infomation");
		
		var now = timeProvider.now();
		if(now.isAfter(reset.getIssuedAt().plusSeconds(300))) throw new IllegalArgumentException("the reset password is expired");
		
		var email = reset.getEmail();
		var user = userService.getByUsername(email);
		if(user == null) throw new UsernameNotFoundException(email);
		
		var encodedPassword = passwordEncoder.encode(password);
		user.setPassword(encodedPassword);
		userService.save(user);
		resetPasswordRepository.delete(reset);
		log.info("send. email:{}", email);
	}
	
	public boolean isValid(String uuid) {
		var resetPassword = resetPasswordRepository.findByUuid(uuid);
		if(resetPassword == null) return false;
		
		var now = timeProvider.now();
		if(now.isAfter(resetPassword.getIssuedAt().plusSeconds(300))) return false;
		
		return true;
	}
}
