package com.chinjja.talk.domain.chat.dao;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.chinjja.talk.domain.chat.model.Chat;
import com.chinjja.talk.domain.chat.model.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
	ChatMessage findTop1ByChatOrderByInstantDesc(Chat chat);
	List<ChatMessage> findByChatOrderByInstantDesc(Chat chat, Pageable pageable);
	List<ChatMessage> findByChatAndInstantLessThanOrderByInstantDesc(Chat chat, Instant from, Pageable pageable);
	List<ChatMessage> findByChatAndInstantGreaterThanOrderByInstantDesc(Chat chat, Instant from, Pageable pageable);
	void deleteByChat(Chat chat);
}
