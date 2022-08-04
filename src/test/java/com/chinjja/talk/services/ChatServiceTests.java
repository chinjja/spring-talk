package com.chinjja.talk.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;

import com.chinjja.talk.domain.chat.dao.ChatMessageRepository;
import com.chinjja.talk.domain.chat.dao.ChatRepository;
import com.chinjja.talk.domain.chat.dao.ChatUserRepository;
import com.chinjja.talk.domain.chat.dao.DirectChatRepository;
import com.chinjja.talk.domain.chat.dto.InviteUserRequest;
import com.chinjja.talk.domain.chat.dto.NewDirectChatRequest;
import com.chinjja.talk.domain.chat.dto.NewMessageRequest;
import com.chinjja.talk.domain.chat.dto.NewOpenChatRequest;
import com.chinjja.talk.domain.chat.event.ChatEvent;
import com.chinjja.talk.domain.chat.event.ChatMessageEvent;
import com.chinjja.talk.domain.chat.event.ChatUserEvent;
import com.chinjja.talk.domain.chat.exception.NotJoinException;
import com.chinjja.talk.domain.chat.model.Chat;
import com.chinjja.talk.domain.chat.model.ChatMessage;
import com.chinjja.talk.domain.chat.model.ChatUser;
import com.chinjja.talk.domain.chat.model.DirectChat;
import com.chinjja.talk.domain.chat.services.ChatService;
import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.domain.user.services.UserService;
import com.chinjja.talk.domain.utils.Event;

@ExtendWith(MockitoExtension.class)
class ChatServiceTests {
	@Mock
	ChatRepository chatRepository;
	
	@Mock
	DirectChatRepository directChatRepository;
	
	@Mock
	ChatUserRepository chatUserRepository;
	
	@Mock
	ChatMessageRepository chatMessageRepository;

	@Mock
	ApplicationEventPublisher applicationEventPublisher;
	
	@Mock
	UserService userService;
	
	@InjectMocks
	ChatService chatService;
	
	User owner;
	User user;
	Chat chat;
	Chat chat2;
	ChatUser ownerMember;
	ChatUser userMember;
	
	@BeforeEach
	void setUp() {
		owner = User.builder()
				.id(UUID.randomUUID())
				.username("owner")
				.password("1234")
				.build();
		

		user = User.builder()
				.id(UUID.randomUUID())
				.username("user")
				.password("1234")
				.build();
		
		chat = Chat.builder()
				.id(UUID.randomUUID())
				.title("chat1")
				.owner(owner)
				.visible(true)
				.build();
		
		chat2 = Chat.builder()
				.id(UUID.randomUUID())
				.title("chat2")
				.owner(owner)
				.build();
		
		ownerMember = new ChatUser(chat, owner);
		userMember = new ChatUser(chat, user);
	}
	
	@Test
	void createOpenChat() {
		var chatNoId = Chat.builder()
				.visible(true)
				.joinable(true)
				.title("chat1")
				.description("desc")
				.owner(owner)
				.build();
		var chat = chatNoId.toBuilder().id(UUID.randomUUID()).build();
		var chatUser = new ChatUser(chat, owner);
		
		when(chatRepository.save(chatNoId)).thenReturn(chat);
		when(chatUserRepository.save(chatUser)).thenReturn(chatUser);
		
		var dto = NewOpenChatRequest.builder()
				.title("chat1")
				.description("desc")
				.visible(true)
				.build();
		var actual = chatService.createOpenChat(owner, dto);
		assertEquals(chat, actual);
		
		verify(chatRepository).save(chatNoId);
		verify(chatUserRepository).save(chatUser);
		
		verify(applicationEventPublisher).publishEvent(new ChatEvent(Event.ADDED, owner, chat));
		verify(applicationEventPublisher).publishEvent(new ChatUserEvent(Event.ADDED, chatUser));
		verifyNoMoreInteractions(applicationEventPublisher);
	}
	
	@Test
	void whenDirectChatExists_thenShouldThrowException() {
		var d = DirectChat.builder()
				.user1(user)
				.user2(owner)
				.build();
		when(directChatRepository.findByUser1AndUser2(any(), any())).thenReturn(d);
		
		assertThrows(Exception.class, () -> {
			chatService.createDirectChat(user, NewDirectChatRequest.builder()
					.username("other")
					.build());
		});
	}
	
	@Test
	void createDirectChat() {
		var chatNoId = Chat.builder()
				.build();
		var chat = chatNoId.toBuilder().id(UUID.randomUUID()).build();
		
		var directChat = DirectChat.builder()
				.chat(chat)
				.user1(owner)
				.user2(user)
				.build();
		
		var chatUser1 = new ChatUser(chat, owner);
		var chatUser2 = new ChatUser(chat, user);
		when(userService.getByUsername("other")).thenReturn(user);
		when(directChatRepository.save(directChat)).thenReturn(directChat);
		when(chatRepository.save(chatNoId)).thenReturn(chat);
		when(chatUserRepository.save(chatUser1)).thenReturn(chatUser1);
		when(chatUserRepository.save(chatUser2)).thenReturn(chatUser2);
		
		var savedChat = chatService.createDirectChat(owner, NewDirectChatRequest.builder()
				.username("other")
				.build());
		
		assertEquals(chat, savedChat);
		
		verify(directChatRepository).findByUser1AndUser2(owner, user);
		verify(directChatRepository).save(directChat);
		verify(chatRepository).save(chatNoId);
		verify(chatUserRepository).save(chatUser1);
		verify(chatUserRepository).save(chatUser2);
		
		verify(applicationEventPublisher).publishEvent(new ChatEvent(Event.ADDED, owner, chat));
		verify(applicationEventPublisher).publishEvent(new ChatEvent(Event.ADDED, user, chat));
		verify(applicationEventPublisher).publishEvent(new ChatUserEvent(Event.ADDED, chatUser1));
		verify(applicationEventPublisher).publishEvent(new ChatUserEvent(Event.ADDED, chatUser2));
		verifyNoMoreInteractions(applicationEventPublisher);
	}
	
	@Test
	void deleteChat() {
		when(chatUserRepository.findByChat(any())).thenReturn(Arrays.asList(ownerMember, userMember));
		
		chatService.deleteChat(chat, owner);
		
		verify(chatUserRepository).delete(ownerMember);
		verify(chatUserRepository).delete(userMember);
		verify(chatRepository).delete(chat);
		
		verify(applicationEventPublisher).publishEvent(new ChatEvent(Event.REMOVED, owner, chat));
		verify(applicationEventPublisher).publishEvent(new ChatEvent(Event.REMOVED, user, chat));
		verify(applicationEventPublisher).publishEvent(new ChatUserEvent(Event.REMOVED, ownerMember));
		verify(applicationEventPublisher).publishEvent(new ChatUserEvent(Event.REMOVED, userMember));
		verifyNoMoreInteractions(applicationEventPublisher);
	}
	
	@Test
	void whenUserIsNotOwner_thenShouldFail() {
		assertThrows(Exception.class, () -> {
			chatService.deleteChat(chat, user);
		});
	}
	
	@Test
	void joinToChat() {
		var chat = Chat.builder()
				.id(UUID.randomUUID())
				.joinable(true)
				.build();
		var chatUser = new ChatUser(chat, user);
		when(chatUserRepository.save(chatUser)).thenReturn(chatUser);
		
		chatService.joinToChat(chat, user);
		
		verify(chatUserRepository).save(chatUser);
		
		verify(applicationEventPublisher).publishEvent(new ChatEvent(Event.ADDED, user, chat));
		verify(applicationEventPublisher).publishEvent(new ChatUserEvent(Event.ADDED, chatUser));
		verifyNoMoreInteractions(applicationEventPublisher);
	}
	
	@Test
	void whenChatIsNotJoinable_thenJoiningShouldFail() {
		assertThrows(IllegalArgumentException.class, () -> {
			chatService.joinToChat(Chat.builder()
					.joinable(false)
					.build(), user);
		});
	}
	
	@Test
	void whenChatIsNotJoinable_thenLeavingShouldFail() {
		assertThrows(IllegalArgumentException.class, () -> {
			chatService.leaveFromChat(Chat.builder()
					.joinable(false)
					.build(), user);
		});
	}
	
	@Test
	void whenMemberIsJoined_thenThrowsException() {
		var chat = Chat.builder()
				.joinable(true)
				.build();
		when(chatUserRepository.existsByChatAndUser(chat, user)).thenReturn(true);
		assertThrows(Exception.class, () -> {
			chatService.joinToChat(chat, user);
		});
		verify(chatUserRepository, never()).save(any());
	}
	
	@Test
	void leaveFromChat() {
		var chat = Chat.builder()
				.id(UUID.randomUUID())
				.joinable(true)
				.build();
		var chatUser = new ChatUser(chat, user);
		when(chatUserRepository.findByChatAndUser(chat, user)).thenReturn(chatUser);
		
		chatService.leaveFromChat(chat, user);
		verify(chatUserRepository).delete(chatUser);
		
		verify(applicationEventPublisher).publishEvent(new ChatEvent(Event.REMOVED, user, chat));
		verify(applicationEventPublisher).publishEvent(new ChatUserEvent(Event.REMOVED, chatUser));
		verifyNoMoreInteractions(applicationEventPublisher);
	}
	
	@Test
	void whenUserIsNotJoined_thenShouldFail() {
		assertThrows(Exception.class, () -> {
			chatService.leaveFromChat(chat, owner);
		});
		verify(chatUserRepository, never()).delete(any());
	}
	
	@Test
	void ownerCannotLeaveFromChat() {
		assertThrows(IllegalArgumentException.class, () -> {
			chatService.leaveFromChat(chat, owner);
		});
		verify(chatRepository, never()).delete(chat);
	}
	
	@Test
	void sendMessage() {
		var message = ChatMessage.builder()
				.id(UUID.randomUUID())
				.chat(chat)
				.sender(user)
				.message("hello")
				.build();

		when(chatMessageRepository.save(any())).thenReturn(message);
		when(chatUserRepository.existsByChatAndUser(chat, user)).thenReturn(true);
		
		var actual = chatService.sendMessage(chat, user, NewMessageRequest.builder()
				.message("hello")
				.build());
		assertEquals(message, actual);
		
		verify(chatMessageRepository).save(message.toBuilder()
				.id(null)
				.instant(any())
				.build());
		
		verify(applicationEventPublisher).publishEvent(new ChatMessageEvent(Event.ADDED, message));
		verifyNoMoreInteractions(applicationEventPublisher);
	}
	
	@Test
	void getMessageList() {
		var message = ChatMessage.builder()
				.id(UUID.randomUUID())
				.chat(chat)
				.sender(user)
				.message("hello")
				.build();
		
		when(chatMessageRepository.findByChatOrderByInstantDesc(chat, PageRequest.ofSize(50))).thenReturn(Arrays.asList(message));
		when(chatUserRepository.existsByChatAndUser(any(), any())).thenReturn(true);
		
		var actual = chatService.getMessageList(chat, owner, 50);
		assertEquals(Arrays.asList(message), actual);
	}
	
	@Test
	void getUserList() {
		when(chatUserRepository.findByChat(chat)).thenReturn(Arrays.asList(ownerMember, userMember));
		when(chatUserRepository.existsByChatAndUser(any(), any())).thenReturn(true);
		
		var actual = chatService.getUserList(chat, owner);
		assertEquals(Arrays.asList(ownerMember, userMember), actual);
	}
	
	@Test
	void whenUserIsNotJoined_thenShouldThrowNotJoinException() {
		when(chatUserRepository.existsByChatAndUser(any(), any())).thenReturn(false);
		
		assertThrows(NotJoinException.class, () -> {
			chatService.getUserList(chat, owner);
		});
	}
	
	@Test
	void getJoinedChatList() {
		when(chatUserRepository.findByUser(owner)).thenReturn(Arrays.asList(ownerMember));
		
		var actual = chatService.getJoinedChatList(owner);
		assertEquals(Arrays.asList(chat), actual);
	}
	
	@Test
	void getOpenChatList() {
		when(chatRepository.findByVisible(true)).thenReturn(Arrays.asList(chat));
		
		var actual = chatService.getPublicChats();
		assertEquals(Arrays.asList(chat), actual);
	}
	
	@Test
	void invite() {
		var chat = Chat.builder()
				.id(UUID.randomUUID())
				.joinable(true)
				.build();
		
		var user1 = User.builder()
				.id(UUID.randomUUID())
				.username("user1")
				.build();
		
		var user2 = User.builder()
				.id(UUID.randomUUID())
				.username("user2")
				.build();
		var chatUser1 = new ChatUser(chat, user1);
		var chatUser2 = new ChatUser(chat, user2);
		when(userService.getByUsername("user1")).thenReturn(user1);
		when(userService.getByUsername("user2")).thenReturn(user2);
		
		when(chatUserRepository.save(chatUser1)).thenReturn(chatUser1);
		when(chatUserRepository.save(chatUser2)).thenReturn(chatUser2);
		
		chatService.invite(chat, InviteUserRequest.builder()
				.usernameList(Arrays.asList("user1", "user2"))
				.build());
		verify(chatUserRepository).save(chatUser1);
		verify(chatUserRepository).save(chatUser2);

		verify(applicationEventPublisher).publishEvent(new ChatEvent(Event.ADDED, user1, chat));
		verify(applicationEventPublisher).publishEvent(new ChatEvent(Event.ADDED, user2, chat));
		verify(applicationEventPublisher).publishEvent(new ChatUserEvent(Event.ADDED, chatUser1));
		verify(applicationEventPublisher).publishEvent(new ChatUserEvent(Event.ADDED, chatUser2));
		verifyNoMoreInteractions(applicationEventPublisher);
	}
	
	@Test
	void whenIsChatUser_thenReturnMessageById() {
		var chat = Chat.builder()
				.build();
		var message = ChatMessage.builder()
				.chat(chat)
				.build();
		var uuid = UUID.randomUUID();
		when(chatMessageRepository.findById(uuid)).thenReturn(Optional.of(message));
		when(chatUserRepository.existsByChatAndUser(any(), any())).thenReturn(true);
		var res = chatService.getMessage(user, uuid);
		assertEquals(message, res);
	}
	
	@Test
	void whenIsNotChatUser_thenThrow() {
		var chat = Chat.builder()
				.build();
		var message = ChatMessage.builder()
				.chat(chat)
				.build();
		var uuid = UUID.randomUUID();
		when(chatMessageRepository.findById(uuid)).thenReturn(Optional.of(message));
		when(chatUserRepository.existsByChatAndUser(any(), any())).thenReturn(false);
		assertThrows(NotJoinException.class, () -> {
			chatService.getMessage(user, uuid);
		});
	}
}
