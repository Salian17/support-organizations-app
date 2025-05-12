package com.example.supportorganizationsapp.dto.request;

public record SendMessageRequestDTO(
        Long chatId, String content
) {
}
