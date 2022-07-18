package com.chinjja.talk.domain.user.services;

import java.util.Arrays;
import java.util.HashSet;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.chinjja.talk.domain.user.dao.UserRepository;
import com.chinjja.talk.domain.user.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	
	@Transactional
	public User save(User user) {
		if(existsByUsername(user.getUsername())) {
			throw new IllegalArgumentException("already exists username");
		}
		return userRepository.save(user);
	}
	
	public User getById(Long id) {
		return userRepository.findById(id).orElse(null);
	}
	
	public boolean existsByUsername(String username) {
		return userRepository.existsByUsername(username);
	}
	
	public boolean hasAnyUser() {
		return userRepository.existsBy();
	}
	
	public User getByUsername(String username) {
		return userRepository.findByUsername(username);
	}
	
	@Transactional
	public void addRole(User user, String... roles) {
		var list = new HashSet<>(user.getRoles());
		list.addAll(Arrays.asList(roles));
		user.setRoles(list);
		userRepository.save(user);
	}
}
