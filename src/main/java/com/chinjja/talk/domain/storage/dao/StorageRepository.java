package com.chinjja.talk.domain.storage.dao;

import org.springframework.data.repository.Repository;

import com.chinjja.talk.domain.storage.model.Storage;

public interface StorageRepository extends Repository<Storage, String> {
	Storage findById(String id);
	Storage save(Storage storage);
	void deleteById(String id);
	void delete(Storage storage);
}
