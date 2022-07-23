package com.chinjja.talk.domain.chat.dao;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.Repository;

import com.chinjja.talk.domain.chat.model.Chat;

public interface ChatRepository extends Repository<Chat, Long> {
	@EntityGraph(attributePaths = "owner")
	List<Chat> findByVisible(boolean visible);
	
	Chat save(Chat chat);
	Chat findById(Long id);
	void delete(Chat chat);
}
