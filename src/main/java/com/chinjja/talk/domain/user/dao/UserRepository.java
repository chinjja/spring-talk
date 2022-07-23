package com.chinjja.talk.domain.user.dao;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.Repository;

import com.chinjja.talk.domain.user.model.User;

public interface UserRepository extends Repository<User, Long> {
	@EntityGraph(attributePaths = "roles")
	User findByUsername(String username);
	
	boolean existsByUsername(String username);
	boolean existsBy();
	
	User save(User user);
}
