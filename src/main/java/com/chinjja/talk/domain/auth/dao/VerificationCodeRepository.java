package com.chinjja.talk.domain.auth.dao;

import org.springframework.data.repository.CrudRepository;

import com.chinjja.talk.domain.auth.model.VerificationCode;
import com.chinjja.talk.domain.user.model.User;

public interface VerificationCodeRepository extends CrudRepository<VerificationCode, Long> {
	VerificationCode findByUser(User user);
	void deleteByUser(User user);
}
