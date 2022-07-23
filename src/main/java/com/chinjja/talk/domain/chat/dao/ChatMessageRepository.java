package com.chinjja.talk.domain.chat.dao;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.chinjja.talk.domain.chat.model.Chat;
import com.chinjja.talk.domain.chat.model.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
	ChatMessage findTop1ByChatOrderByInstantDesc(Chat chat);
	
	@EntityGraph(attributePaths = "sender")
	List<ChatMessage> findByChatOrderByInstantDesc(Chat chat, Pageable pageable);
	
	@EntityGraph(attributePaths = "sender")
	List<ChatMessage> findByChatAndInstantLessThanOrderByInstantDesc(Chat chat, Instant from, Pageable pageable);
	
	@EntityGraph(attributePaths = "sender")
	List<ChatMessage> findByChatAndInstantGreaterThanOrderByInstantDesc(Chat chat, Instant from, Pageable pageable);
	void deleteByChat(Chat chat);
}
