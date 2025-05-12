package com.example.supportorganizationsapp.dto.request;

import java.util.List;

public record GroupChatRequestDTO(List<Long> userIds, String chatName) {
}
