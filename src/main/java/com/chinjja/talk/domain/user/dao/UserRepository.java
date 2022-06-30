package com.chinjja.talk.domain.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chinjja.talk.domain.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
	boolean existsByUsername(String username);
	boolean existsBy();
}
