package com.example.supportorganizationsapp.dto.request;

import java.util.Objects;

public class SendMessageRequestDTO {
    private final Long chatId;
    private final String content;

    public SendMessageRequestDTO(Long chatId, String content) {
        this.chatId = chatId;
        this.content = content;
    }

    public Long getChatId() {
        return chatId;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SendMessageRequestDTO that = (SendMessageRequestDTO) obj;
        return Objects.equals(chatId, that.chatId) && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, content);
    }

    @Override
    public String toString() {
        return "SendMessageRequestDTO{" +
                "chatId=" + chatId +
                ", content='" + content + '\'' +
                '}';
    }
}