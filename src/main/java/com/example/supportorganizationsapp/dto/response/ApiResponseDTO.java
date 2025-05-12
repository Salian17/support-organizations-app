package com.example.supportorganizationsapp.dto.response;

import lombok.Builder;

@Builder
public record ApiResponseDTO(String message, boolean status) {
}
