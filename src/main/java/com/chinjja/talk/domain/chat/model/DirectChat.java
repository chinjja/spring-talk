package com.chinjja.talk.domain.chat.model;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user1_id", "user2_id"}))
public class DirectChat {
	@EmbeddedId
	Id id;
	
	@OneToOne(optional = false)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	Chat chat;
	
	@MapsId("user1_id")
	@ManyToOne
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	User user1;
	
	@MapsId("user2_id")
	@ManyToOne
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	User user2;
	

	@Builder(toBuilder = true)
	public DirectChat(User user1, User user2, Chat chat) {
		this.id = new Id(user1.getId(), user2.getId());
		this.user1 = user1;
		this.user2 = user2;
		this.chat = chat;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Id implements Serializable {
		@Column(name = "user1_id", nullable = false, columnDefinition = "BINARY(16)")
		UUID user1Id;
		
		@Column(name ="user2_id", nullable = false, columnDefinition = "BINARY(16)")
		UUID user2Id;
	}
}
