package com.chinjja.talk.domain.user.services;

import javax.transaction.Transactional;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.chinjja.talk.domain.user.dao.UserRepository;
import com.chinjja.talk.domain.user.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
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
	
	@Override
	public User loadUserByUsername(String username) throws UsernameNotFoundException {
		var user = userRepository.findByUsername(username);
		if(user == null) throw new UsernameNotFoundException(username);
		return user;
	}
}
