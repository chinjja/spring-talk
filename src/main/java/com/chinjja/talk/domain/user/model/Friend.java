package com.chinjja.talk.domain.user.model;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@Entity
public class Friend {
	@EmbeddedId
	private Id id;
	
	@MapsId("owner_id")
	@ManyToOne
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private User owner;
	
	@MapsId("user_id")
	@ManyToOne
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private User user;
	
	private String name;
	
	public Friend(User owner, User user) {
		this.id = new Id(owner.getId(), user.getId());
		this.owner = owner;
		this.user = user;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Id implements Serializable {
		@Column(name = "owner_id", nullable = false, columnDefinition = "BINARY(16)")
		private UUID ownerId;
		
		@Column(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
		private UUID userId;
	}
}
