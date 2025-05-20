package com.example.supportorganizationsapp.service;

import com.example.supportorganizationsapp.dto.request.UpdateUserRequestDTO;
import com.example.supportorganizationsapp.dto.response.application.ApplicationResponse;
import com.example.supportorganizationsapp.exception.UserException;
import com.example.supportorganizationsapp.models.User;

import java.util.List;

public interface UserService {
    User findUserById(Long id) throws UserException;
    User findUserByProfile(String jwt) throws UserException;
    User updateUser(Long id, UpdateUserRequestDTO request) throws UserException;
    List<User> searchUserByName(String name);
    List<ApplicationResponse> getUserApplications(Long userId) throws UserException;
}
