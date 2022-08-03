package com.chinjja.talk.domain.user.dao;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.Repository;

import com.chinjja.talk.domain.user.model.Friend;
import com.chinjja.talk.domain.user.model.User;

public interface FriendRepository extends Repository<Friend, Long> {
	@EntityGraph(attributePaths = "user")
	List<Friend> findByOwner(User owner);
	@EntityGraph(attributePaths = "owner")
	List<Friend> findByUser(User user);
	int countByOwner(User owner);
	Friend findByOwnerAndUser(User owner, User user);
	boolean existsByOwnerAndUser(User owner, User user);
	Friend save(Friend friend);
	void delete(Friend friend);
}
