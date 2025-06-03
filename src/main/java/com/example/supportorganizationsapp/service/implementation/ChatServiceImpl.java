package com.example.supportorganizationsapp.service.implementation;

import com.example.supportorganizationsapp.dto.request.GroupChatRequestDTO;
import com.example.supportorganizationsapp.exception.ChatException;
import com.example.supportorganizationsapp.exception.UserException;
import com.example.supportorganizationsapp.models.Chat;
import com.example.supportorganizationsapp.models.User;
import com.example.supportorganizationsapp.repository.ChatRepository;
import com.example.supportorganizationsapp.service.ChatService;
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
public class ChatServiceImpl implements ChatService {

    private final UserService userService;
    private final ChatRepository chatRepository;

    @Override
    public Chat createChat(User reqUser, Long userId2) throws UserException {

        User user2 = userService.findUserById(userId2);

        Optional<Chat> existingChatOptional = chatRepository.findSingleChatByUsers(user2, reqUser);

        if (existingChatOptional.isPresent()) {
            return existingChatOptional.get();
        }

        Chat chat = Chat.builder()
                .createdBy(reqUser)
                .users(new HashSet<>(Set.of(reqUser, user2)))
                .isGroup(false)
                .build();

        return chatRepository.save(chat);
    }

    @Override
    public Chat findChatById(Long id) throws ChatException {

        Optional<Chat> chatOptional = chatRepository.findById(id);

        if (chatOptional.isPresent()) {
            return chatOptional.get();
        }

        throw new ChatException("No chat found with id " + id);
    }

    @Override
    public List<Chat> findAllByUserId(Long userId) throws UserException {

        User user = userService.findUserById(userId);

        return chatRepository.findChatByUserId(user.getId()).stream()
                .sorted((chat1, chat2) -> {
                    if (chat1.getMessages().isEmpty() && chat2.getMessages().isEmpty()) {
                        return 0;
                    } else if (chat1.getMessages().isEmpty()) {
                        return 1;
                    } else if (chat2.getMessages().isEmpty()) {
                        return -1;
                    }
                    LocalDateTime timeStamp1 = chat1.getMessages().get(chat1.getMessages().size() - 1).getTimeStamp();
                    LocalDateTime timeStamp2 = chat2.getMessages().get(chat2.getMessages().size() - 1).getTimeStamp();
                    return timeStamp2.compareTo(timeStamp1);
                })
                .toList();
    }

    @Override
    public Chat createGroup(GroupChatRequestDTO req, User reqUser) throws UserException {

        Chat groupChat = Chat.builder()
                .isGroup(true)
                .chatName(req.getChatName())
                .createdBy(reqUser)
                .admins(new HashSet<>(Set.of(reqUser)))
                .users(new HashSet<>())
                .build();

        for (Long userId : req.getUserIds()) {
            User userToAdd = userService.findUserById(userId);
            groupChat.getUsers().add(userToAdd);
        }

        return chatRepository.save(groupChat);
    }

    @Override
    public Chat addUserToGroup(Long userId, Long chatId, User reqUser) throws UserException, ChatException {

        Chat chat = findChatById(chatId);
        User user = userService.findUserById(userId);

        if (chat.getAdmins().contains(reqUser)) {
            chat.getUsers().add(user);
            return chatRepository.save(chat);
        }

        throw new UserException("User doesn't have permissions to add members to group chat");
    }

    @Override
    public Chat renameGroup(Long chatId, String groupName, User reqUser) throws UserException, ChatException {

        Chat chat = findChatById(chatId);

        if (chat.getAdmins().contains(reqUser)) {
            chat.setChatName(groupName);
            return chatRepository.save(chat);
        }

        throw new UserException("User doesn't have permissions to rename group chat");
    }

    @Override
    public Chat removeFromGroup(Long chatId, Long userId, User reqUser) throws UserException, ChatException {

        Chat chat = findChatById(chatId);
        User user = userService.findUserById(userId);

        boolean isAdminOrRemoveSelf = chat.getAdmins().contains(reqUser) ||
                (chat.getUsers().contains(reqUser) && user.getId().equals(reqUser.getId()));

        if (isAdminOrRemoveSelf) {
            chat.getUsers().remove(user);
            return chatRepository.save(chat);
        }

        throw new UserException("User doesn't have permissions to remove users from group chat");
    }

    @Override
    public void deleteChat(Long chatId, Long userId) throws UserException, ChatException {

        Chat chat = findChatById(chatId);
        User user = userService.findUserById(userId);

        boolean isSingleChatOrAdmin = !chat.getIsGroup() || chat.getAdmins().contains(user);

        if (isSingleChatOrAdmin) {
            chatRepository.deleteById(chatId);
            return;
        }

        throw new UserException("User doesn't have permissions to delete group chat");
    }

    @Override
    public Chat markAsRead(Long chatId, User reqUser) throws ChatException, UserException {

        Chat chat = findChatById(chatId);

        if (chat.getUsers().contains(reqUser)) {
            chat.getMessages().forEach(msg -> msg.getReadBy().add(reqUser.getId()));

            return chatRepository.save(chat);
        }


        throw new UserException("User is not related to chat");
    }

    @Override
    public Chat promoteToAdmin(Long chatId, Long userId, User reqUser) throws UserException, ChatException {

        Chat chat = findChatById(chatId);
        User userToPromote = userService.findUserById(userId);

        if (!chat.getIsGroup()) {
            throw new ChatException("Cannot promote admin in private chat");
        }

        if (!chat.getCreatedBy().getId().equals(reqUser.getId())) {
            throw new UserException("Only chat creator can promote users to admin");
        }

        if (!chat.getUsers().contains(userToPromote)) {
            throw new UserException("User is not a member of this chat");
        }

        if (chat.getAdmins().contains(userToPromote)) {
            throw new UserException("User is already an admin");
        }

        chat.getAdmins().add(userToPromote);
        return chatRepository.save(chat);
    }

    @Override
    public Chat transferOwnership(Long chatId, Long newOwnerId, User reqUser) throws UserException, ChatException {

        Chat chat = findChatById(chatId);
        User newOwner = userService.findUserById(newOwnerId);

        if (!chat.getIsGroup()) {
            throw new ChatException("Cannot transfer ownership of private chat");
        }

        if (!chat.getCreatedBy().getId().equals(reqUser.getId())) {
            throw new UserException("Only chat creator can transfer ownership");
        }

        if (!chat.getUsers().contains(newOwner)) {
            throw new UserException("New owner must be a member of the chat");
        }

        if (newOwner.getId().equals(reqUser.getId())) {
            throw new UserException("Cannot transfer ownership to yourself");
        }

        chat.setCreatedBy(newOwner);

        if (!chat.getAdmins().contains(newOwner)) {
            chat.getAdmins().add(newOwner);
        }

        return chatRepository.save(chat);
    }

    @Override
    public Set<User> getChatAdmins(Long chatId, User reqUser) throws ChatException, UserException {

        Chat chat = findChatById(chatId);

        if (!chat.getUsers().contains(reqUser) && !chat.getAdmins().contains(reqUser)) {
            throw new UserException("User is not a member of this chat");
        }

        if (!chat.getIsGroup()) {
            throw new ChatException("Private chats don't have admins");
        }

        return chat.getAdmins();
    }

    @Override
    public List<Chat> searchChatsByName(String name, User reqUser) throws UserException {

        if (name == null || name.trim().isEmpty()) {
            throw new UserException("Search name cannot be empty");
        }

        return chatRepository.findChatsByNameContainingAndUser(name.trim(), reqUser);
    }

    @Override
    public List<Chat> findChatsWithUser(Long targetUserId, User reqUser) throws UserException {

        User targetUser = userService.findUserById(targetUserId);

        if (targetUser.getId().equals(reqUser.getId())) {
            throw new UserException("Cannot search chats with yourself");
        }

        return chatRepository.findChatsWithUserByRequestingUser(targetUser, reqUser);
    }

}
