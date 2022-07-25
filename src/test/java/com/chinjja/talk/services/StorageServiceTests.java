package com.chinjja.talk.services;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.chinjja.talk.domain.storage.dao.StorageRepository;
import com.chinjja.talk.domain.storage.model.Storage;
import com.chinjja.talk.domain.storage.services.StorageService;

@ExtendWith(MockitoExtension.class)
public class StorageServiceTests {
	@InjectMocks
	StorageService storageService;
	
	@Mock StorageRepository storageRepository;
	
	String id;
	byte[] data = new byte[1024];
	Storage storage;
	
	@BeforeEach
	void setUp() {
		id = UUID.randomUUID().toString();
		storage = Storage.builder()
				.id(id)
				.data(data)
				.build();
	}
	
	@Test
	void save() {
		when(storageRepository.save(storage)).thenReturn(storage);
		
		storageService.save(storage);
		
		verify(storageRepository).save(storage);
		verifyNoMoreInteractions(storageRepository);
	}
	
	@Test
	void get() {
		when(storageRepository.findById(id)).thenReturn(storage);
		
		storageService.getById(id);
		
		verify(storageRepository).findById(id);
		verifyNoMoreInteractions(storageRepository);
	}
	
	@Test
	void deleteById() {
		storageService.deleteById(id);
		
		verify(storageRepository).deleteById(id);
		verifyNoMoreInteractions(storageRepository);
	}
	
	@Test
	void delete() {
		storageService.delete(storage);
		
		verify(storageRepository).delete(storage);
		verifyNoMoreInteractions(storageRepository);
	}
}
