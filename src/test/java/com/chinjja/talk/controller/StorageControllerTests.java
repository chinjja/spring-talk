package com.chinjja.talk.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.chinjja.talk.domain.storage.controller.StorageController;
import com.chinjja.talk.domain.storage.dto.SaveStorageRequest;
import com.chinjja.talk.domain.storage.dto.SaveStorageResponse;
import com.chinjja.talk.domain.storage.dto.StorageDto;
import com.chinjja.talk.domain.storage.model.Storage;
import com.chinjja.talk.domain.storage.services.StorageService;
import com.chinjja.talk.domain.user.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(StorageController.class)
@AutoConfigureMockMvc(addFilters = false)
@EnableJpaRepositories
public class StorageControllerTests {
	@MockBean
	StorageService storageService;
	
	@MockBean
	UserService userService;
	
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	String id;
	byte[] data;
	Storage storage;
	
	@BeforeEach
	void setUp() {
		id = UUID.randomUUID().toString();
		data = "data".getBytes();
		storage = Storage.builder()
				.id(id)
				.data(data)
				.build();
	}
	
	@Test
	void save() throws Exception {
		var req = SaveStorageRequest.builder()
				.id(id)
				.data(data)
				.build();
		var res = SaveStorageResponse.builder()
				.id(id)
				.build();
				
		
		when(storageService.save(storage)).thenReturn(storage);
		
		mockMvc.perform(post("/api/storage")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(req)))
		.andExpect(status().isCreated())
		.andExpect(content().json(objectMapper.writeValueAsString(res)));
		
		verify(storageService).save(storage);
		verifyNoMoreInteractions(storageService);
	}
	
	@Test
	void getById() throws Exception {
		var res = StorageDto.builder()
				.data(data)
				.build();
		
		when(storageService.getById(id)).thenReturn(storage);
		
		mockMvc.perform(get("/api/storage/"+id))
		.andExpect(status().isOk())
		.andExpect(content().json(objectMapper.writeValueAsString(res)));
		
		verify(storageService).getById(id);
		verifyNoMoreInteractions(storageService);
	}
	
	@Test
	void deleteById() throws Exception {
		mockMvc.perform(delete("/api/storage/"+id))
		.andExpect(status().isOk())
		.andExpect(content().string(""));
		
		verify(storageService).deleteById(id);
		verifyNoMoreInteractions(storageService);
	}
}
