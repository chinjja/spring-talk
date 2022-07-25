package com.chinjja.talk.domain.user.services;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chinjja.talk.domain.storage.model.Storage;
import com.chinjja.talk.domain.storage.services.StorageService;
import com.chinjja.talk.domain.user.dao.UserRepository;
import com.chinjja.talk.domain.user.dto.UpdateProfileRequest;
import com.chinjja.talk.domain.user.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
	private final UserRepository userRepository;
	private final StorageService storageService;
	
	@Transactional
	public User save(User user) {
		user = userRepository.save(user);
		log.info("save. {}", user);
		return user;
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
		user = userRepository.save(user);
		log.info("add role success. {}", user);
	}
	
	@Transactional
	public User updateProfile(User user, UpdateProfileRequest request) {
		if(request.getName() != null) {
			user.setName(request.getName());
		}
		if(request.getState() != null) {
			user.setState(request.getState());
		}
		if(request.getPhoto() != null) {
			var storage = storageService.save(Storage.builder()
					.id(user.getUsername()+"/photo")
					.data(request.getPhoto())
					.build());
			user.setPhotoId(storage.getId());
		}
		return save(user);
	}
}
