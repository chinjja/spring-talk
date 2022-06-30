package com.chinjja.talk.domain.user.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chinjja.talk.domain.user.model.Friend;
import com.chinjja.talk.domain.user.model.User;

public interface FriendRepository extends JpaRepository<Friend, Long> {
	List<Friend> findByUser(User user);
	int countByUser(User user);
	Friend findByUserAndOther(User user, User other);
	boolean existsByUserAndOther(User user, User other);
}
