package com.example.supportorganizationsapp.service.implementation;

import com.example.supportorganizationsapp.dto.request.SendMessageRequestDTO;
import com.example.supportorganizationsapp.exception.ChatException;
import com.example.supportorganizationsapp.exception.MessageException;
import com.example.supportorganizationsapp.exception.UserException;
import com.example.supportorganizationsapp.models.Chat;
import com.example.supportorganizationsapp.models.Message;
import com.example.supportorganizationsapp.models.User;
import com.example.supportorganizationsapp.repository.MessageRepository;
import com.example.supportorganizationsapp.service.ChatService;
import com.example.supportorganizationsapp.service.MessageService;
import com.example.supportorganizationsapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final UserService userService;
    private final ChatService chatService;
    private final MessageRepository messageRepository;

    @Override
    public Message sendMessage(SendMessageRequestDTO req, Long userId) throws UserException, ChatException {

        User user = userService.findUserById(userId);
        Chat chat = chatService.findChatById(req.getChatId());

        Message message = Message.builder()
                .chat(chat)
                .user(user)
                .content(req.getContent())
                .timeStamp(LocalDateTime.now())
                .readBy(new HashSet<>(Set.of(user.getId())))
                .build();

        chat.getMessages().add(message);

        return messageRepository.save(message);
    }

    @Override
    public List<Message> getChatMessages(Long chatId, User reqUser) throws UserException, ChatException {

        Chat chat = chatService.findChatById(chatId);

        if (!chat.getUsers().contains(reqUser)) {
            throw new UserException("User isn't related to chat " + chatId);
        }

        return messageRepository.findByChat_Id(chat.getId());
    }

    @Override
    public Message findMessageById(Long messageId) throws MessageException {

        Optional<Message> message = messageRepository.findById(messageId);

        if (message.isPresent()) {
            return message.get();
        }

        throw new MessageException("Message not found " + messageId);
    }

    @Override
    public void deleteMessageById(Long messageId, User reqUser) throws UserException, MessageException {

        Message message = findMessageById(messageId);

        if (message.getUser().getId().equals(reqUser.getId())) {
            messageRepository.deleteById(messageId);
            return;
        }

        throw new UserException("User is not related to message " + message.getId());
    }
    @Override
    public Message updateMessageContent(Long messageId, String newContent, Long userId) throws UserException, MessageException {
        User user = userService.findUserById(userId);
        Message message = findMessageById(messageId);

        if (!message.getUser().getId().equals(user.getId())) {
            throw new UserException("User is not authorized to update this message");
        }

        message.setContent(newContent);
        return messageRepository.save(message);
    }

    @Override
    public Message markMessageAsRead(Long messageId, Long userId) throws UserException, MessageException {
        User user = userService.findUserById(userId);
        Message message = findMessageById(messageId);

        // Проверяем, что пользователь состоит в чате
        if (!message.getChat().getUsers().contains(user)) {
            throw new UserException("User is not a member of this chat");
        }

        message.getReadBy().add(user.getId());
        return messageRepository.save(message);
    }

    @Override
    public List<Message> searchMessagesByContent(String searchText, Long chatId, Long userId) throws UserException, ChatException {
        User user = userService.findUserById(userId);
        Chat chat = chatService.findChatById(chatId);

        if (!chat.getUsers().contains(user)) {
            throw new UserException("User is not a member of this chat");
        }

        return messageRepository.findByChat_IdAndContentContainingIgnoreCase(chatId, searchText);
    }

    @Override
    public Message getLastMessageFromUser(Long userId, Long chatId, Long reqUserId) throws UserException, ChatException, MessageException {
        User reqUser = userService.findUserById(reqUserId);
        Chat chat = chatService.findChatById(chatId);

        if (!chat.getUsers().contains(reqUser)) {
            throw new UserException("User is not a member of this chat");
        }

        List<Message> messages = messageRepository.findByChat_IdAndUser_IdOrderByTimeStampDesc(chatId, userId);

        if (messages.isEmpty()) {
            throw new MessageException("No messages found from user " + userId + " in chat " + chatId);
        }

        return messages.get(0);
    }
}
