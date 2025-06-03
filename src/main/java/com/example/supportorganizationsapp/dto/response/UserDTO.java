package com.example.supportorganizationsapp.dto.response;

import com.example.supportorganizationsapp.models.User;
import lombok.Builder;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDTO {
    private final Long id;
    private final String email;
    private final String phoneNumber;
    private final String firstName;
    private final String lastName;
    private final String role;

    private UserDTO(Builder builder) {
        this.id = builder.id;
        this.email = builder.email;
        this.phoneNumber = builder.phoneNumber;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.role = builder.role;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRole() {
        return role;
    }

    public static Builder builder() {
        return new Builder();
    }

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserDTO userDTO = (UserDTO) obj;
        return Objects.equals(id, userDTO.id) &&
                Objects.equals(email, userDTO.email) &&
                Objects.equals(phoneNumber, userDTO.phoneNumber) &&
                Objects.equals(firstName, userDTO.firstName) &&
                Objects.equals(lastName, userDTO.lastName) &&
                Objects.equals(role, userDTO.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, phoneNumber, firstName, lastName, role);
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    public static class Builder {
        private Long id;
        private String email;
        private String phoneNumber;
        private String firstName;
        private String lastName;
        private String role;

        private Builder() {}

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public UserDTO build() {
            return new UserDTO(this);
        }
    }
}