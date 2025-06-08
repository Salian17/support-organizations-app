package com.example.supportorganizationsapp.dto.response;

import com.example.supportorganizationsapp.models.Message;

import java.time.LocalDateTime;
import java.util.*;

public class MessageDTO {
    private final Long id;
    private final String content;
    private final LocalDateTime timeStamp;
    private final UserDTO user;
    private final Set<Long> readBy;

    private MessageDTO(Builder builder) {
        this.id = builder.id;
        this.content = builder.content;
        this.timeStamp = builder.timeStamp;
        this.user = builder.user;
        this.readBy = builder.readBy != null ? new HashSet<>(builder.readBy) : new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public UserDTO getUser() {
        return user;
    }

    public Set<Long> getReadBy() {
        return new HashSet<>(readBy);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static MessageDTO fromMessage(Message message) {
        if (Objects.isNull(message)) return null;
        return MessageDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .timeStamp(message.getTimeStamp())
                .user(UserDTO.fromUser(message.getUser()))
                .readBy(new HashSet<>(message.getReadBy()))
                .build();
    }

    public static List<MessageDTO> fromMessages(Collection<Message> messages) {
        if (Objects.isNull(messages)) return List.of();
        return messages.stream()
                .map(MessageDTO::fromMessage)
                .toList();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MessageDTO that = (MessageDTO) obj;
        return Objects.equals(id, that.id) &&
                Objects.equals(content, that.content) &&
                Objects.equals(timeStamp, that.timeStamp) &&
                Objects.equals(user, that.user) &&
                Objects.equals(readBy, that.readBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, timeStamp, user, readBy);
    }

    @Override
    public String toString() {
        return "MessageDTO{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", timeStamp=" + timeStamp +
                ", user=" + user +
                ", readBy=" + readBy +
                '}';
    }

    public static class Builder {
        private Long id;
        private String content;
        private LocalDateTime timeStamp;
        private UserDTO user;
        private Set<Long> readBy;

        private Builder() {}

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder timeStamp(LocalDateTime timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public Builder user(UserDTO user) {
            this.user = user;
            return this;
        }

        public Builder readBy(Set<Long> readBy) {
            this.readBy = readBy;
            return this;
        }

        public MessageDTO build() {
            return new MessageDTO(this);
        }
    }
}
