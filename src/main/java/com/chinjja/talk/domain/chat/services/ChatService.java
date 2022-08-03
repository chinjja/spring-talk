package com.chinjja.talk.domain.chat.services;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chinjja.talk.domain.chat.dao.ChatMessageRepository;
import com.chinjja.talk.domain.chat.dao.ChatRepository;
import com.chinjja.talk.domain.chat.dao.ChatUserRepository;
import com.chinjja.talk.domain.chat.dao.DirectChatRepository;
import com.chinjja.talk.domain.chat.dto.ChatInfoDto;
import com.chinjja.talk.domain.chat.dto.ChatMessageDto;
import com.chinjja.talk.domain.chat.dto.InviteUserRequest;
import com.chinjja.talk.domain.chat.dto.NewDirectChatRequest;
import com.chinjja.talk.domain.chat.dto.NewGroupChatRequest;
import com.chinjja.talk.domain.chat.dto.NewMessageRequest;
import com.chinjja.talk.domain.chat.dto.NewOpenChatRequest;
import com.chinjja.talk.domain.chat.exception.AlreadyJoinException;
import com.chinjja.talk.domain.chat.exception.NotJoinException;
import com.chinjja.talk.domain.chat.model.Chat;
import com.chinjja.talk.domain.chat.model.ChatMessage;
import com.chinjja.talk.domain.chat.model.ChatUser;
import com.chinjja.talk.domain.chat.model.DirectChat;
import com.chinjja.talk.domain.event.event.ChatEvent;
import com.chinjja.talk.domain.event.event.ChatMessageEvent;
import com.chinjja.talk.domain.event.event.ChatUserEvent;
import com.chinjja.talk.domain.event.event.Event;
import com.chinjja.talk.domain.user.model.User;
import com.chinjja.talk.domain.user.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {
	private final ChatRepository chatRepository;
	private final DirectChatRepository directChatRepository;
	private final ChatUserRepository chatUserRepository;
	private final ChatMessageRepository chatMessageRepository;
	private final UserService userService;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final ModelMapper modelMapper;
	
	@Transactional
	public Chat createOpenChat(User owner, NewOpenChatRequest dto) {
		var chat = chatRepository.save(Chat.builder()
				.owner(owner)
				.visible(dto.isVisible())
				.joinable(true)
				.title(dto.getTitle())
				.description(dto.getDescription())
				.build());
		
		join(chat, owner);
		return chat;
	}
	
	@Transactional
	public Chat createDirectChat(User user, NewDirectChatRequest dto) {
		var other = userService.getByUsername(dto.getUsername());
		var directChat = getDirectChat(user, other);
		if(directChat != null) {
			throw new IllegalArgumentException("already exists direct chat");
		}
		var chat = chatRepository.save(Chat.builder()
				.visible(false)
				.joinable(false)
				.build());
		
		directChat = directChatRepository.save(DirectChat.builder()
				.chat(chat)
				.user1(user)
				.user2(other)
				.build());
		
		join(chat, user);
		join(chat, other);
		return chat;
	}
	
	@Transactional
	public Chat createGroupChat(User user, NewGroupChatRequest dto) {
		var chat = chatRepository.save(Chat.builder()
				.visible(false)
				.joinable(true)
				.title(dto.getTitle())
				.build());
		
		var users = dto.getUsernameList().stream()
				.map(x -> userService.getByUsername(x))
				.collect(Collectors.toList());
		for(User u : users) {
			join(chat, u);
		}
		return chat;
	}
	
	@Transactional
	public void deleteChat(Chat chat, User auth) {
		if(!auth.equals(chat.getOwner())) {
			throw new IllegalArgumentException("only owner delete this chat");
		}
		var list = chatUserRepository.findByChat(chat);
		for(var user : list) {
			leave(user);
		}
		chatMessageRepository.deleteByChat(chat);
		chatRepository.delete(chat);
	}
	
	public DirectChat getDirectChat(User user1, User user2) {
		var chat = directChatRepository.findByUser1AndUser2(user1, user2);
		if(chat != null) return chat;
		return directChatRepository.findByUser1AndUser2(user2, user1);
	}
	
	public List<Chat> getJoinedChatList(User user) {
		return chatUserRepository.findByUser(user).stream()
				.map(x -> x.getChat())
				.collect(Collectors.toList());
	}
	
	public List<Chat> getPublicChats() {
		return chatRepository.findByVisible(true);
	}
	
	public Chat getChat(Long id) {
		return chatRepository.findById(id);
	}
	
	public ChatUser getChatUser(Chat chat, User user) {
		return chatUserRepository.findByChatAndUser(chat, user);
	}
	
	public List<ChatUser> getUserList(Chat chat, User user) {
		checkJoin(chat, user);
		return chatUserRepository.findByChat(chat);
	}
	
	public int getUserCount(Chat chat, User user) {
		checkJoin(chat, user);
		return chatUserRepository.countByChat(chat);
	}
	
	@Transactional
	public void invite(Chat chat, InviteUserRequest dto) {
		var users = dto.getUsernameList().stream()
				.map(x -> userService.getByUsername(x))
				.collect(Collectors.toList());
		for(var user : users) {
			join(chat, user);
		}
	}
	
	@Transactional
	public ChatUser joinToChat(Chat chat, User user) {
		if(!chat.isJoinable()) {
			throw new IllegalArgumentException("cannot join this chat");
		}
		checkNotJoin(chat, user);
		var chatUser = join(chat, user);
		return chatUser;
	}
	
	@Transactional
	private ChatUser join(Chat chat, User user) {
		var chatUser = chatUserRepository.save(new ChatUser(chat, user));

		applicationEventPublisher.publishEvent(new ChatEvent(Event.ADDED, user, chat));
		applicationEventPublisher.publishEvent(new ChatUserEvent(Event.ADDED, chatUser));
		return chatUser;
	}
	
	@Transactional
	public void leaveFromChat(Chat chat, User auth) {
		if(auth.equals(chat.getOwner())) {
			throw new IllegalArgumentException("owner can not leave this chat");
		}
		if(!chat.isJoinable()) {
			throw new IllegalArgumentException("cannot leave this chat");
		}
		var chatUser = getChatUser(chat, auth);
		if(chatUser == null) {
			throw new NotJoinException();
		}
		leave(chatUser);
		if(!chatUserRepository.existsByChat(chat)) {
			chatMessageRepository.deleteByChat(chat);
			chatRepository.delete(chat);
		}
	}
	
	@Transactional
	private void leave(ChatUser chatUser) {
		chatUserRepository.delete(chatUser);
		applicationEventPublisher.publishEvent(new ChatEvent(Event.REMOVED, chatUser.getUser(), chatUser.getChat()));
		applicationEventPublisher.publishEvent(new ChatUserEvent(Event.REMOVED, chatUser));
	}
	
	public boolean isJoin(Chat chat, User user) {
		return chatUserRepository.existsByChatAndUser(chat, user);
	}
	
	@Transactional
	public ChatMessage sendMessage(Chat chat, User sender, NewMessageRequest dto) {
		checkJoin(chat, sender);
		var chatMessage = chatMessageRepository.save(ChatMessage.builder()
				.chat(chat)
				.sender(sender)
				.message(dto.getMessage())
				.build());
		applicationEventPublisher.publishEvent(new ChatMessageEvent(Event.ADDED, chatMessage));
		return chatMessage;
	}
	
	public List<ChatMessage> getMessageList(Chat chat, User user, int limit) {
		checkJoin(chat, user);
		return chatMessageRepository.findByChatOrderByInstantDesc(chat, PageRequest.ofSize(limit));
	}
	
	public List<ChatMessage> getMessageList(Chat chat, User user, Instant from, int limit) {
		checkJoin(chat, user);
		return chatMessageRepository.findByChatAndInstantLessThanOrderByInstantDesc(chat, from, PageRequest.ofSize(limit));
	}
	
	public ChatMessage getMessage(User auth, long id) {
		var message = chatMessageRepository.findById(id).get();
		var chat = message.getChat();
		checkJoin(chat, auth);
		return message;
	}
	
	public ChatMessage getLatestMessage(Chat chat, User user) {
		checkJoin(chat, user);
		return chatMessageRepository.findTop1ByChatOrderByInstantDesc(chat);
	}
	
	public int getUnreadMessageCount(Chat chat, User user, int limit) {
		var chatUser = getChatUser(chat, user);
		checkJoin(chat, user);
		var list = chatMessageRepository.findByChatAndInstantGreaterThanOrderByInstantDesc(chat, chatUser.getReadAt(), PageRequest.ofSize(limit));
		return list.size();
	}
	
	@Transactional
	public ChatUser read(Chat chat, User user) {
		var chatUser = getChatUser(chat, user);
		checkJoin(chat, user);
		chatUser.setReadAt(Instant.now());
		chatUser = chatUserRepository.save(chatUser);

		applicationEventPublisher.publishEvent(new ChatUserEvent(Event.UPDATED, chatUser));
		return chatUser;
	}
	
	public ChatInfoDto getChatInfo(Chat chat, User user) {
		var unreadCount = getUnreadMessageCount(chat, user, 100);
		var userCount = getUserCount(chat, user);
		var message = getLatestMessage(chat, user);
		var messageDto = message == null ? null : modelMapper.map(message, ChatMessageDto.class);
		return ChatInfoDto.builder()
				.unreadCount(unreadCount)
				.userCount(userCount)
				.latestMessage(messageDto)
				.build();
	}
	
	private void checkJoin(Chat chat, User user) {
		if(!isJoin(chat, user)) {
			throw new NotJoinException();
		}
	}
	
	private void checkNotJoin(Chat chat, User user) {
		if(isJoin(chat, user)) {
			throw new AlreadyJoinException();
		}
	}
}
