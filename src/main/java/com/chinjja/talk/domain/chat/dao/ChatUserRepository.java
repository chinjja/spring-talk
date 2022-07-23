package com.chinjja.talk.domain.chat.dao;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.Repository;

import com.chinjja.talk.domain.chat.model.Chat;
import com.chinjja.talk.domain.chat.model.ChatUser;
import com.chinjja.talk.domain.user.model.User;

public interface ChatUserRepository extends Repository<ChatUser, Long> {
	int countByChat(Chat chat);
	
	@EntityGraph(attributePaths = "user")
	List<ChatUser> findByChat(Chat chat);
	ChatUser findByChatAndUser(Chat chat, User user);
	boolean existsByChatAndUser(Chat chat, User user);
	boolean existsByChat(Chat chat);
	ChatUser save(ChatUser chatUser);
	void delete(ChatUser chatUser);
	
	@EntityGraph(attributePaths = {"chat", "chat.owner"})
	List<ChatUser> findByUser(User user);
}
