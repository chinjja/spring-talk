package com.chinjja.talk.domain.user.services;

import java.util.List;
import java.util.stream.Collectors;

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
	public Friend addFriend(User user, AddFriendRequest request) {
		var username = request.getUsername();
		if(user.getUsername().equals(username)) {
			throw new IllegalArgumentException("cannat self friend");
		}
		var other = userService.getByUsername(username);
		if(isFriend(user, other)) {
			throw new IllegalArgumentException("already friend");
		}
		applicationEventPublisher.publishEvent(new FriendAdded(user, other));
		
		var friend = friendRepository.save(Friend.builder()
				.user(user)
				.other(other)
				.build());
		log.info("add friend. {}", friend);
		return friend;
	}

	@Transactional
	public void removeFriend(User user, User other) {
		var friend = friendRepository.findByUserAndOther(user, other);
		if(friend == null) {
			throw new IllegalArgumentException("not friend");
		}
		friendRepository.delete(friend);
		applicationEventPublisher.publishEvent(new FriendDeleted(user, friend.getOther()));
		log.info("remove friend. {}", friend);
	}
	
	public boolean isFriend(User user, User other) {
		return friendRepository.existsByUserAndOther(user, other);
	}
	
	public List<User> getFriends(User user) {
		var data = friendRepository.findByUser(user);
		return data.stream().map(x -> x.getOther()).collect(Collectors.toList());
	}
	
	public User getFriend(User user, User other) {
		if(!isFriend(user, other)) {
			throw new IllegalArgumentException("no friend");
		}
		return other;
	}
}
