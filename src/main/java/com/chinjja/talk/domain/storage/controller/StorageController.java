package com.chinjja.talk.domain.storage.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chinjja.talk.domain.storage.services.StorageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
public class StorageController {
	private final StorageService storageService;
	
	@GetMapping("/{id}")
	public byte[] get(@PathVariable("id") String id) {
		var storage = storageService.getById(id);
		return storage.getData();
	}
}
