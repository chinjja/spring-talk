package com.chinjja.talk.domain.chat.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chinjja.talk.domain.chat.model.Chat;
import com.chinjja.talk.domain.chat.model.ChatUser;
import com.chinjja.talk.domain.user.model.User;

public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {
	int countByChat(Chat chat);
	List<ChatUser> findByChat(Chat chat);
	List<ChatUser> findByIdIn(List<Long> ids);
	ChatUser findByChatAndUser(Chat chat, User user);
	boolean existsByChatAndUser(Chat chat, User user);
	boolean existsByChat(Chat chat);
}
