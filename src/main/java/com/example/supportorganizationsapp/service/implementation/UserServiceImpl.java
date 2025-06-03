package com.example.supportorganizationsapp.service.implementation;

import com.example.supportorganizationsapp.config.JwtConstants;
import com.example.supportorganizationsapp.config.TokenProvider;
import com.example.supportorganizationsapp.dto.request.UpdateUserRequestDTO;
import com.example.supportorganizationsapp.dto.response.application.ApplicationResponse;
import com.example.supportorganizationsapp.enums.RoleEnum;
import com.example.supportorganizationsapp.exception.UserException;
import com.example.supportorganizationsapp.models.User;
import com.example.supportorganizationsapp.repository.UserRepository;
import com.example.supportorganizationsapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    @Override
    public User findUserById(Long id) throws UserException {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        throw new UserException("User not found with id " + id);
    }

    @Override
    public User findUserByProfile(String jwt) throws UserException {
        String email = String.valueOf(tokenProvider.getClaimsFromToken(jwt).get(JwtConstants.EMAIL));
        if (email == null) {
            throw new BadCredentialsException("Invalid token");
        }
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get();
        }
        throw new UserException("User not found with email " + email);
    }

    @Override
    public User updateUser(Long id, UpdateUserRequestDTO request) throws UserException {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User existingUser = user.get();
            existingUser.setEmail(request.getEmail());
            existingUser.setPhoneNumber(request.getPhoneNumber());
            existingUser.setFirstName(request.getFirstName());
            existingUser.setLastName(request.getLastName());
            return userRepository.save(existingUser);
        } else {
            throw new UserException("User not found with id " + id);
        }
    }

    @Override
    public List<User> searchUserByName(String name) {
        return userRepository.findByFirstName(name).stream()
                .sorted(Comparator.comparing(User::getFirstName))
                .toList();
    }

    @Override
    public List<ApplicationResponse> getUserApplications(Long userId) throws UserException {
        User user = findUserById(userId);
        return user.getApplications().stream()
                .map(application -> new ApplicationResponse(
                        application.getId(),
                        application.getDate(),
                        application.getTime(),
                        application.getDepartureStation(),
                        application.getDestinationStation(),
                        application.getComment(),
                        application.getStatus(),
                        application.getPassenger().getId(),
                        application.getCompanion() != null ? application.getCompanion().getId() : null
                ))
                .toList();
    }
    @Override
    public User findUserByEmail(String email) throws UserException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get();
        }
        throw new UserException("User not found with email " + email);
    }
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getUsersByRole(RoleEnum role) {
        return userRepository.findByRoleEnum(role);
    }

    @Override
    public void deleteUser(Long id) throws UserException {
        if (!userRepository.existsById(id)) {
            throw new UserException("User not found with id " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public User findUserByPhoneNumber(String phoneNumber) throws UserException {
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        if (user.isPresent()) {
            return user.get();
        }
        throw new UserException("User not found with phone number " + phoneNumber);
    }

    @Override
    public List<User> findUsersByFullName(String firstName, String lastName) {
        return userRepository.findByFirstNameAndLastName(firstName, lastName);
    }
}
