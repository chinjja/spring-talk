package com.chinjja.talk.domain.storage.services;

import org.springframework.stereotype.Service;

import com.chinjja.talk.domain.storage.dao.StorageRepository;
import com.chinjja.talk.domain.storage.model.Storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {
	private final StorageRepository storageRepository;
	
	public Storage getById(String id) {
		return storageRepository.findById(id);
	}
	
	public Storage save(Storage storage) {
		log.info("save storage. {}", storage);
		return storageRepository.save(storage);
	}
	
	public void delete(Storage storage) {
		storageRepository.delete(storage);
	}
	
	public void deleteById(String id) {
		storageRepository.deleteById(id);
	}
}
