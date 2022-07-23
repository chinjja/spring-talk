package com.chinjja.talk.domain.chat.dao;

import org.springframework.data.repository.Repository;

import com.chinjja.talk.domain.chat.model.DirectChat;
import com.chinjja.talk.domain.user.model.User;

public interface DirectChatRepository extends Repository<DirectChat, Long> {
	DirectChat findByUser1AndUser2(User user1, User user2);
	boolean existsByUser1AndUser2(User user1, User user2);
	DirectChat save(DirectChat chat);
	void delete(DirectChat chat);
}
