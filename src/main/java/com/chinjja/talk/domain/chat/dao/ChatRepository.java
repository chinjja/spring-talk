package com.chinjja.talk.domain.chat.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.chinjja.talk.domain.chat.model.Chat;
import com.chinjja.talk.domain.user.model.User;

public interface ChatRepository extends JpaRepository<Chat, Long> {
	List<Chat> findByVisible(boolean visible);
	
	@Query("select a.chat from ChatUser a where a.user=:user")
	List<Chat> findJoinedChats(User user);
}
