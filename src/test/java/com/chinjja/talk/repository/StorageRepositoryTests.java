package com.chinjja.talk.repository;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.chinjja.talk.domain.storage.dao.StorageRepository;
import com.chinjja.talk.domain.storage.model.Storage;

@DataJpaTest
public class StorageRepositoryTests {
	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	StorageRepository storageRepository;
	
	String id;
	byte[] data = new byte[1024];
	
	@BeforeEach
	void setUp() {
		id = UUID.randomUUID().toString();
	}
	
	@Test
	void save() {
		var saved = storageRepository.save(Storage.builder()
				.id(id)
				.data(data)
				.build());
		entityManager.flush();
		entityManager.clear();
		
		assertEquals(id, saved.getId());
		assertArrayEquals(data, saved.getData());
	}
	
	@Test
	void duplicate() {
		save();
		save();
	}
	
	@Test
	void findById() {
		save();
		var loaded = storageRepository.findById(id);
		
		assertEquals(id, loaded.getId());
		assertArrayEquals(data, loaded.getData());
	}
	
	@Test
	void deleteById() {
		save();
		storageRepository.deleteById(id);
		assertNull(storageRepository.findById(id));
	}
	
	@Test
	void delete() {
		save();
		var storage = storageRepository.findById(id);
		storageRepository.delete(storage);
		entityManager.flush();
		entityManager.clear();
		assertNull(storageRepository.findById(id));
	}
}
