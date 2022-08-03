package com.chinjja.talk.domain.user.services;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chinjja.talk.domain.event.event.Event;
import com.chinjja.talk.domain.event.event.FriendEvent;
import com.chinjja.talk.domain.user.dao.FriendRepository;
import com.chinjja.talk.domain.user.dao.UserRepository;
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
	private final UserRepository userRepository;
	
	@Transactional
	public Friend addFriend(User owner, AddFriendRequest request) {
		var username = request.getUsername();
		if(owner.getUsername().equals(username)) {
			throw new IllegalArgumentException("cannat self friend");
		}
		var user = userRepository.findByUsername(username);
		if(isFriend(owner, user)) {
			throw new IllegalArgumentException("already friend");
		}
		
		var friend = friendRepository.save(new Friend(owner, user));
		applicationEventPublisher.publishEvent(new FriendEvent(Event.ADDED, friend));
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
		applicationEventPublisher.publishEvent(new FriendEvent(Event.REMOVED, friend));
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
	
	public List<Friend> getFollowers(User user) {
		return friendRepository.findByUser(user);
	}
	
	public List<Friend> getFollowers(Friend user) {
		return friendRepository.findByUser(user.getUser());
	}
	
	@Transactional
	public void updateName(Friend friend, String name) {
		log.info("update friend name. {}, {}", friend, name);
		friend.setName(name);
		friendRepository.save(friend);
		var followers = getFollowers(friend);
		for(var follower : followers) {
			applicationEventPublisher.publishEvent(new FriendEvent(Event.UPDATED, follower));
		}
	}
}
