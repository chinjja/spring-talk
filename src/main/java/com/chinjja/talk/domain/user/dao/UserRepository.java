package com.chinjja.talk.domain.user.dao;

import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.Repository;

import com.chinjja.talk.domain.user.model.User;

public interface UserRepository extends Repository<User, UUID> {
	@EntityGraph(attributePaths = "roles")
	User findByUsername(String username);

	@EntityGraph(attributePaths = "roles")
	User findById(UUID username);
	
	boolean existsByUsername(String username);
	boolean existsById(UUID id);
	boolean existsBy();
	
	User save(User user);
	
	void delete(User user);
	void deleteById(UUID id);
}
