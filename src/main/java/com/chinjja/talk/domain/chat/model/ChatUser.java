package com.chinjja.talk.domain.chat.model;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.PrePersist;

import com.chinjja.talk.domain.user.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class ChatUser {
	@EmbeddedId
	private Id id;
	
	@MapsId("chat_id")
	@ManyToOne
	private Chat chat;

	@MapsId("user_id")
	@ManyToOne
	private User user;

	@Column(nullable = false)
	private Instant readAt;
	
	public ChatUser(Chat chat, User user) {
		this.id = new Id(chat.getId(), user.getId());
		this.chat = chat;
		this.user = user;
	}

	@PrePersist
	void readAt() {
		if(readAt != null) return;
		readAt = Instant.now().truncatedTo(ChronoUnit.MICROS);
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Id implements Serializable {
		@Column(name = "chat_id")
		private long chatId;
		
		@Column(name ="user_id")
		private long userId;
	}
}
