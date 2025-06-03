package com.example.supportorganizationsapp.dto.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GroupChatRequestDTO {
    private final List<Long> userIds;
    private final String chatName;

    public GroupChatRequestDTO(List<Long> userIds, String chatName) {
        this.userIds = userIds != null ? new ArrayList<>(userIds) : new ArrayList<>();
        this.chatName = chatName;
    }

    public List<Long> getUserIds() {
        return new ArrayList<>(userIds);
    }

    public String getChatName() {
        return chatName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GroupChatRequestDTO that = (GroupChatRequestDTO) obj;
        return Objects.equals(userIds, that.userIds) && Objects.equals(chatName, that.chatName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userIds, chatName);
    }

    @Override
    public String toString() {
        return "GroupChatRequestDTO{" +
                "userIds=" + userIds +
                ", chatName='" + chatName + '\'' +
                '}';
    }
}
