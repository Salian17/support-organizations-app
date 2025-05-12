package com.example.supportorganizationsapp.dto.response;

import com.example.supportorganizationsapp.models.User;
import lombok.Builder;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
public record UserDTO(
        Long id,
        String email,
        String phoneNumber,
        String firstName,
        String lastName,
        String role) {

    public static UserDTO fromUser(User user) {
        if (Objects.isNull(user)) return null;
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRoleEnum().getNameRole())
                .build();
    }

    public static Set<UserDTO> fromUsers(Collection<User> users) {
        if (Objects.isNull(users)) return Set.of();
        return users.stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toSet());
    }

    public static List<UserDTO> fromUsersAsList(Collection<User> users) {
        if (Objects.isNull(users)) return List.of();
        return users.stream()
                .map(UserDTO::fromUser)
                .toList();
    }
}
