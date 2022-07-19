package com.chinjja.talk.domain.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.chinjja.talk.domain.user.dto.AddFriendRequest;
import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.domain.user.services.FriendService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {
	private final FriendService friendService;
	
	@GetMapping
	public List<User> getFriends(@AuthenticationPrincipal User user) {
		return friendService.getFriends(user);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public User addFriend(
			@AuthenticationPrincipal User user,
			@RequestBody AddFriendRequest dto) {
		log.info("add friend. {}, {}", user, dto);
		var friend = friendService.addFriend(user, dto);
		return friend.getOther();
	}
	
	@DeleteMapping("/{username}")
	public void removeFriend(
			@AuthenticationPrincipal User user,
			@PathVariable("username") User other) {
		log.info("remove friend. {}, {}", user, other);
		friendService.removeFriend(user, other);
	}
	
	@GetMapping("/{username}")
	public User getFriend(
			@AuthenticationPrincipal User user,
			@PathVariable("username") User other) {
		return friendService.getFriend(user, other);
	}
}
