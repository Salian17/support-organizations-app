package com.example.supportorganizationsapp.service;

import com.example.supportorganizationsapp.dto.request.GroupChatRequestDTO;
import com.example.supportorganizationsapp.exception.ChatException;
import com.example.supportorganizationsapp.exception.UserException;
import com.example.supportorganizationsapp.models.Chat;
import com.example.supportorganizationsapp.models.User;

import java.util.List;
import java.util.Set;

public interface ChatService {
    Chat createChat(User reqUser, Long userId2) throws UserException;
    Chat findChatById(Long id) throws ChatException;
    List<Chat> findAllByUserId(Long userId) throws UserException;
    Chat createGroup(GroupChatRequestDTO req, User reqUser) throws UserException;
    Chat addUserToGroup(Long userId, Long chatId, User reqUser) throws UserException, ChatException;
    Chat renameGroup(Long chatId, String groupName, User reqUser) throws UserException, ChatException;
    Chat removeFromGroup(Long chatId, Long userId, User reqUser) throws UserException, ChatException;
    void deleteChat(Long chatId, Long userId) throws UserException, ChatException;
    Chat markAsRead(Long chatId, User reqUser) throws ChatException, UserException;
    Chat promoteToAdmin(Long chatId, Long userId, User reqUser) throws UserException, ChatException;
    Chat transferOwnership(Long chatId, Long newOwnerId, User reqUser) throws UserException, ChatException;
    Set<User> getChatAdmins(Long chatId, User reqUser) throws ChatException, UserException;
    List<Chat> searchChatsByName(String name, User reqUser) throws UserException;
    List<Chat> findChatsWithUser(Long targetUserId, User reqUser) throws UserException;
}
