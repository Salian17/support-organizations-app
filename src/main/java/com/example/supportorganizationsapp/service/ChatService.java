package com.example.supportorganizationsapp.service;

import com.example.supportorganizationsapp.dto.request.GroupChatRequestDTO;
import com.example.supportorganizationsapp.exception.ChatException;
import com.example.supportorganizationsapp.exception.UserException;
import com.example.supportorganizationsapp.models.Chat;
import com.example.supportorganizationsapp.models.User;

import java.util.List;

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

}
