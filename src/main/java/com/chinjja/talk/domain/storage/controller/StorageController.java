package com.chinjja.talk.domain.storage.controller;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.chinjja.talk.domain.storage.dto.SaveStorageRequest;
import com.chinjja.talk.domain.storage.dto.SaveStorageResponse;
import com.chinjja.talk.domain.storage.dto.StorageDto;
import com.chinjja.talk.domain.storage.model.Storage;
import com.chinjja.talk.domain.storage.services.StorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
public class StorageController {
	private final StorageService storageService;
	private final ModelMapper modelMapper;
	
	@GetMapping("/{id}")
	public StorageDto get(@PathVariable("id") String id) {
		var storage = storageService.getById(id);
		return modelMapper.map(storage, StorageDto.class);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public SaveStorageResponse save(@RequestBody SaveStorageRequest request) {
		log.info("save storage request {}", request);
		var storage = modelMapper.map(request, Storage.class);
		storage = storageService.save(storage);
		return modelMapper.map(storage, SaveStorageResponse.class);
	}
	
	@DeleteMapping("/{id}")
	public void delete(@PathVariable("id") String id) {
		log.info("delete storage request {}", id);
		storageService.deleteById(id);
	}
}
