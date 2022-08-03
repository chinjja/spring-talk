package com.chinjja.talk.domain.user.services;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chinjja.talk.domain.storage.model.Storage;
import com.chinjja.talk.domain.storage.services.StorageService;
import com.chinjja.talk.domain.user.dao.UserRepository;
import com.chinjja.talk.domain.user.dto.UpdateProfileRequest;
import com.chinjja.talk.domain.user.event.FriendEvent;
import com.chinjja.talk.domain.user.event.UserEvent;
import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.domain.utils.Event;
import com.chinjja.talk.domain.utils.UuidProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
	private final UuidProvider uuidProvider;
	private final UserRepository userRepository;
	private final StorageService storageService;
	private final FriendService friendService;
	private final ApplicationEventPublisher applicationEventPublisher;
	
	@Transactional
	public User save(User user) {
		user = userRepository.save(user);
		log.info("save. {}", user);
		applicationEventPublisher.publishEvent(new UserEvent(Event.UPDATED, user));
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
		log.info("add roles. {}, {}", user, roles);
		var list = new HashSet<>(user.getRoles());
		list.addAll(Arrays.asList(roles));
		user.setRoles(list);
		user = save(user);
	}
	
	@Transactional
	public User updateProfile(User user, UpdateProfileRequest request) {
		log.info("update profil.: {}, {}", user, request);
		boolean changed = false;
		if(request.getName() != null) {
			user.setName(request.getName());
			changed = true;
		}
		if(request.getState() != null) {
			user.setState(request.getState());
			changed = true;
		}
		if(request.getPhoto() != null) {
			if(user.getPhotoId() != null) {
				storageService.deleteById(user.getPhotoId());
			}
			var id = uuidProvider.random().toString();
			var storage = storageService.save(Storage.builder()
					.id(id)
					.data(request.getPhoto())
					.build());
			user.setPhotoId(storage.getId());
			changed = true;
		}
		if(changed) {
			user = save(user);
			var followers = friendService.getFollowers(user);
			for(var follower : followers) {
				applicationEventPublisher.publishEvent(new FriendEvent(Event.UPDATED,follower));
			}
		}
		return user;
	}
}
