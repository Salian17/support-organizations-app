package com.example.supportorganizationsapp.service;

import com.example.supportorganizationsapp.dto.request.SendMessageRequestDTO;
import com.example.supportorganizationsapp.exception.ChatException;
import com.example.supportorganizationsapp.exception.MessageException;
import com.example.supportorganizationsapp.exception.UserException;
import com.example.supportorganizationsapp.models.Message;
import com.example.supportorganizationsapp.models.User;

import java.util.List;

public interface MessageService {
    Message sendMessage(SendMessageRequestDTO req, Long userId) throws UserException, ChatException;
    List<Message> getChatMessages(Long chatId, User reqUser) throws UserException, ChatException;
    Message findMessageById(Long messageId) throws MessageException;
    void deleteMessageById(Long messageId, User reqUser) throws UserException, MessageException;
    Message updateMessageContent(Long messageId, String newContent, Long userId) throws UserException, MessageException;
    Message markMessageAsRead(Long messageId, Long userId) throws UserException, MessageException;
    List<Message> searchMessagesByContent(String searchText, Long chatId, Long userId) throws UserException, ChatException;
    Message getLastMessageFromUser(Long userId, Long chatId, Long reqUserId) throws UserException, ChatException, MessageException;
}

