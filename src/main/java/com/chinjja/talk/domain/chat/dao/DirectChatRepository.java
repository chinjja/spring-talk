package com.chinjja.talk.domain.chat.dao;

import org.springframework.data.repository.CrudRepository;

import com.chinjja.talk.domain.chat.model.DirectChat;
import com.chinjja.talk.domain.user.model.User;

public interface DirectChatRepository extends CrudRepository<DirectChat, DirectChat.Id> {
	DirectChat findByUser1AndUser2(User user1, User user2);
	boolean existsByUser1AndUser2(User user1, User user2);
}
