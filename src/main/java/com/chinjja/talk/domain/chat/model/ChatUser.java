package com.chinjja.talk.domain.chat.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.chinjja.talk.domain.user.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"chat_id", "user_id"}))
public class ChatUser {
	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne(optional = false)
	private Chat chat;

	@ManyToOne(optional = false)
	private User user;

	@Column(nullable = false)
	private Instant readAt;

	@PrePersist
	void readAt() {
		if(readAt != null) return;
		readAt = Instant.now().truncatedTo(ChronoUnit.MICROS);
	}
}
