package com.chinjja.talk.domain.user.services;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chinjja.talk.domain.event.event.FriendAdded;
import com.chinjja.talk.domain.event.event.FriendDeleted;
import com.chinjja.talk.domain.user.dao.FriendRepository;
import com.chinjja.talk.domain.user.dto.AddFriendRequest;
import com.chinjja.talk.domain.user.model.Friend;
import com.chinjja.talk.domain.user.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {
	private final FriendRepository friendRepository;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final UserService userService;
	
	@Transactional
	public Friend addFriend(User owner, AddFriendRequest request) {
		var username = request.getUsername();
		if(owner.getUsername().equals(username)) {
			throw new IllegalArgumentException("cannat self friend");
		}
		var user = userService.getByUsername(username);
		if(isFriend(owner, user)) {
			throw new IllegalArgumentException("already friend");
		}
		
		var friend = friendRepository.save(new Friend(owner, user));
		applicationEventPublisher.publishEvent(new FriendAdded(owner, friend));
		log.info("add friend. {}", friend);
		return friend;
	}

	@Transactional
	public void removeFriend(User owner, User user) {
		var friend = getFriend(owner, user);
		if(friend == null) {
			throw new IllegalArgumentException("not friend");
		}
		friendRepository.delete(friend);
		applicationEventPublisher.publishEvent(new FriendDeleted(owner, user));
		log.info("remove friend. {}", user);
	}
	
	public boolean isFriend(User owner, User user) {
		return friendRepository.existsByOwnerAndUser(owner, user);
	}
	
	public List<Friend> getFriends(User owner) {
		return friendRepository.findByOwner(owner);
	}
	
	public Friend getFriend(User owner, User user) {
		return friendRepository.findByOwnerAndUser(owner, user);
	}
}
