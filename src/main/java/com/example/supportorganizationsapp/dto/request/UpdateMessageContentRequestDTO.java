package com.example.supportorganizationsapp.dto.request;

import java.util.Objects;

public class UpdateMessageContentRequestDTO {
    private final String content;

    public UpdateMessageContentRequestDTO(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UpdateMessageContentRequestDTO that = (UpdateMessageContentRequestDTO) obj;
        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }

    @Override
    public String toString() {
        return "UpdateMessageContentRequestDTO{" +
                "content='" + content + '\'' +
                '}';
    }
}