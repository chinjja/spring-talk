package com.chinjja.talk.domain.chat.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.chinjja.talk.domain.user.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(indexes = @Index(columnList = "chat_id, instant"))
public class ChatMessage {
	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne(optional = false)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private Chat chat;
	
	@ManyToOne(optional = false)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private User sender;

	@Column(nullable = false)
	private String message;
	
	@Column(nullable = false)
	private Instant instant;
	
	@PrePersist
	private void instant() {
		if(instant != null) return;
		instant = Instant.now().truncatedTo(ChronoUnit.MICROS);
	}
}
