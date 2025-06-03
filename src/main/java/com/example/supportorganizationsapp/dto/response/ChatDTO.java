package com.example.supportorganizationsapp.dto.response;

import com.example.supportorganizationsapp.models.Chat;
import lombok.Builder;

import java.util.*;

public class ChatDTO {
    private final Long id;
    private final String chatName;
    private final Boolean isGroup;
    private final Set<UserDTO> admins;
    private final Set<UserDTO> users;
    private final UserDTO createdBy;
    private final List<MessageDTO> messages;

    private ChatDTO(Builder builder) {
        this.id = builder.id;
        this.chatName = builder.chatName;
        this.isGroup = builder.isGroup;
        this.admins = builder.admins != null ? new HashSet<>(builder.admins) : new HashSet<>();
        this.users = builder.users != null ? new HashSet<>(builder.users) : new HashSet<>();
        this.createdBy = builder.createdBy;
        this.messages = builder.messages != null ? new ArrayList<>(builder.messages) : new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public String getChatName() {
        return chatName;
    }

    public Boolean getIsGroup() {
        return isGroup;
    }

    public Set<UserDTO> getAdmins() {
        return new HashSet<>(admins);
    }

    public Set<UserDTO> getUsers() {
        return new HashSet<>(users);
    }

    public UserDTO getCreatedBy() {
        return createdBy;
    }

    public List<MessageDTO> getMessages() {
        return new ArrayList<>(messages);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static ChatDTO fromChat(Chat chat) {
        if (Objects.isNull(chat)) return null;
        return ChatDTO.builder()
                .id(chat.getId())
                .chatName(chat.getChatName())
                .isGroup(chat.getIsGroup())
                .admins(UserDTO.fromUsers(chat.getAdmins()))
                .users(UserDTO.fromUsers(chat.getUsers()))
                .createdBy(UserDTO.fromUser(chat.getCreatedBy()))
                .messages(MessageDTO.fromMessages(chat.getMessages()))
                .build();
    }

    public static List<ChatDTO> fromChats(Collection<Chat> chats) {
        if (Objects.isNull(chats)) return List.of();
        return chats.stream()
                .map(ChatDTO::fromChat)
                .toList();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ChatDTO chatDTO = (ChatDTO) obj;
        return Objects.equals(id, chatDTO.id) &&
                Objects.equals(chatName, chatDTO.chatName) &&
                Objects.equals(isGroup, chatDTO.isGroup) &&
                Objects.equals(admins, chatDTO.admins) &&
                Objects.equals(users, chatDTO.users) &&
                Objects.equals(createdBy, chatDTO.createdBy) &&
                Objects.equals(messages, chatDTO.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatName, isGroup, admins, users, createdBy, messages);
    }

    @Override
    public String toString() {
        return "ChatDTO{" +
                "id=" + id +
                ", chatName='" + chatName + '\'' +
                ", isGroup=" + isGroup +
                ", admins=" + admins +
                ", users=" + users +
                ", createdBy=" + createdBy +
                ", messages=" + messages +
                '}';
    }

    public static class Builder {
        private Long id;
        private String chatName;
        private Boolean isGroup;
        private Set<UserDTO> admins;
        private Set<UserDTO> users;
        private UserDTO createdBy;
        private List<MessageDTO> messages;

        private Builder() {}

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder chatName(String chatName) {
            this.chatName = chatName;
            return this;
        }

        public Builder isGroup(Boolean isGroup) {
            this.isGroup = isGroup;
            return this;
        }

        public Builder admins(Set<UserDTO> admins) {
            this.admins = admins;
            return this;
        }

        public Builder users(Set<UserDTO> users) {
            this.users = users;
            return this;
        }

        public Builder createdBy(UserDTO createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder messages(List<MessageDTO> messages) {
            this.messages = messages;
            return this;
        }

        public ChatDTO build() {
            return new ChatDTO(this);
        }
    }
}
