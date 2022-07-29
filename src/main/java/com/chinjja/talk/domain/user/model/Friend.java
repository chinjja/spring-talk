package com.chinjja.talk.domain.user.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Friend {
	@EmbeddedId
	private Id id;
	
	@MapsId("owner_id")
	@ManyToOne
	private User owner;
	
	@MapsId("user_id")
	@ManyToOne
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
		@Column(name = "owner_id")
		private long ownerId;
		
		@Column(name = "user_id")
		private long userId;
	}
}
