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
}
