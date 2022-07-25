package com.chinjja.talk.domain.auth.dao;

import org.springframework.data.repository.Repository;

import com.chinjja.talk.domain.auth.model.ResetPassword;

public interface ResetPasswordRepository extends Repository<ResetPassword, String> {
	ResetPassword save(ResetPassword resetPassword);
	ResetPassword findById(String email);
	ResetPassword findByUuid(String uuid);
	void delete(ResetPassword resetPassword);
}
