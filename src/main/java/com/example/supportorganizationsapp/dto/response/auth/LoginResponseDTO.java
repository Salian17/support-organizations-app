package com.example.supportorganizationsapp.dto.response.auth;

import lombok.Builder;

@Builder
public record LoginResponseDTO(Long userId, String accessToken, String refreshToken, boolean isAuthenticated) {
}
