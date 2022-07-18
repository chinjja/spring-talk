package com.chinjja.talk.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

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
import com.chinjja.talk.domain.chat.exception.NotJoinException;
import com.chinjja.talk.domain.chat.model.Chat;
import com.chinjja.talk.domain.chat.model.ChatMessage;
import com.chinjja.talk.domain.chat.model.ChatUser;
import com.chinjja.talk.domain.chat.model.DirectChat;
import com.chinjja.talk.domain.chat.services.ChatService;
import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.domain.user.services.UserService;

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
				.id(1L)
				.username("owner")
				.password("1234")
				.build();
		

		user = User.builder()
				.id(2L)
				.username("user")
				.password("1234")
				.build();
		
		chat = Chat.builder()
				.id(1L)
				.title("chat1")
				.owner(owner)
				.visible(true)
				.build();
		
		chat2 = Chat.builder()
				.id(1L)
				.title("chat2")
				.owner(owner)
				.build();
		
		ownerMember = ChatUser.builder()
				.id(1L)
				.chat(chat)
				.user(owner)
				.build();
		
		userMember = ChatUser.builder()
				.id(2L)
				.chat(chat)
				.user(user)
				.build();
	}
	
	@Test
	void createOpenChat() {
		var chat = Chat.builder()
				.visible(true)
				.joinable(true)
				.title("chat1")
				.description("desc")
				.owner(owner)
				.build();
		
		var chatUser = ChatUser.builder()
				.chat(chat)
				.user(owner)
				.build();
		
		when(chatRepository.save(chat)).thenReturn(chat);
		when(chatUserRepository.save(chatUser)).thenReturn(chatUser);
		
		var dto = NewOpenChatRequest.builder()
				.title("chat1")
				.description("desc")
				.visible(true)
				.build();
		var actual = chatService.createOpenChat(owner, dto);
		assertEquals(chat, actual);
		
		verify(chatRepository).save(chat);
		verify(chatUserRepository).save(chatUser);
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
		var chat = Chat.builder()
				.build();
		
		var directChat = DirectChat.builder()
				.chat(chat)
				.user1(owner)
				.user2(user)
				.build();
		
		when(userService.getByUsername("other")).thenReturn(user);
		when(directChatRepository.save(directChat)).thenReturn(directChat);
		when(chatRepository.save(chat)).thenReturn(chat);
		
		var savedChat = chatService.createDirectChat(owner, NewDirectChatRequest.builder()
				.username("other")
				.build());
		
		assertEquals(chat, savedChat);
		
		verify(directChatRepository).findByUser1AndUser2(owner, user);
		verify(directChatRepository).save(directChat);
		verify(chatRepository).save(chat);
		verify(chatUserRepository).save(ChatUser.builder()
				.chat(chat)
				.user(user)
				.build());
		verify(chatUserRepository).save(ChatUser.builder()
				.chat(chat)
				.user(owner)
				.build());
	}
	
	@Test
	void deleteChat() {
		when(chatUserRepository.findByChat(any())).thenReturn(Arrays.asList(ownerMember, userMember));
		
		chatService.deleteChat(chat, owner);
		
		verify(chatUserRepository).delete(ownerMember);
		verify(chatUserRepository).delete(userMember);
		verify(chatRepository).delete(chat);
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
				.joinable(true)
				.build();
		chatService.joinToChat(chat, user);
		verify(chatUserRepository).save(ChatUser.builder()
				.chat(chat)
				.user(user)
				.build());
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
				.joinable(true)
				.build();
		when(chatUserRepository.findByChatAndUser(chat, owner)).thenReturn(userMember);
		chatService.leaveFromChat(chat, owner);
		verify(chatUserRepository).delete(userMember);
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
				.id(1L)
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
	}
	
	@Test
	void getMessageList() {
		var message = ChatMessage.builder()
				.id(1L)
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
		when(chatRepository.findJoinedChats(owner)).thenReturn(Arrays.asList(chat));
		
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
				.joinable(true)
				.build();
		when(userService.getByUsername(user.getUsername())).thenReturn(user);
		when(chatUserRepository.save(any())).thenReturn(userMember);
		chatService.invite(chat, InviteUserRequest.builder()
				.usernameList(Arrays.asList(user.getUsername()))
				.build());
		verify(chatUserRepository).save(ChatUser.builder()
				.chat(chat)
				.user(user)
				.build());
	}
	
	@Test
	void whenIsChatUser_thenReturnMessageById() {
		var chat = Chat.builder()
				.build();
		var message = ChatMessage.builder()
				.chat(chat)
				.build();
		when(chatMessageRepository.findById(1L)).thenReturn(Optional.of(message));
		when(chatUserRepository.existsByChatAndUser(any(), any())).thenReturn(true);
		var res = chatService.getMessage(user, 1);
		assertEquals(message, res);
	}
	
	@Test
	void whenIsNotChatUser_thenThrow() {
		var chat = Chat.builder()
				.build();
		var message = ChatMessage.builder()
				.chat(chat)
				.build();
		when(chatMessageRepository.findById(1L)).thenReturn(Optional.of(message));
		when(chatUserRepository.existsByChatAndUser(any(), any())).thenReturn(false);
		assertThrows(NotJoinException.class, () -> {
			chatService.getMessage(user, 1);
		});
	}
}
