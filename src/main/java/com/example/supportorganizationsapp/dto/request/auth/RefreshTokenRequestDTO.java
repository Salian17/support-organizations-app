package com.example.supportorganizationsapp.dto.request.auth;

import java.util.Objects;

public class RefreshTokenRequestDTO {
    private final String refreshToken;

    public RefreshTokenRequestDTO(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RefreshTokenRequestDTO that = (RefreshTokenRequestDTO) obj;
        return Objects.equals(refreshToken, that.refreshToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(refreshToken);
    }

    @Override
    public String toString() {
        return "RefreshTokenRequestDTO{" +
                "refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
