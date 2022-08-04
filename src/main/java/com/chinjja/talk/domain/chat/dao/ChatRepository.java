package com.chinjja.talk.domain.chat.dao;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import com.chinjja.talk.domain.chat.model.Chat;

public interface ChatRepository extends CrudRepository<Chat, UUID> {
	@EntityGraph(attributePaths = "owner")
	List<Chat> findByVisible(boolean visible);
}
