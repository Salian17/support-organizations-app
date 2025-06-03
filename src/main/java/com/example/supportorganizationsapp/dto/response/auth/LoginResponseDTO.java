package com.example.supportorganizationsapp.dto.response.auth;

import lombok.Builder;

import java.util.Objects;

public class LoginResponseDTO {
    private final Long userId;
    private final String accessToken;
    private final String refreshToken;
    private final boolean isAuthenticated;

    private LoginResponseDTO(Builder builder) {
        this.userId = builder.userId;
        this.accessToken = builder.accessToken;
        this.refreshToken = builder.refreshToken;
        this.isAuthenticated = builder.isAuthenticated;
    }

    public Long getUserId() {
        return userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LoginResponseDTO that = (LoginResponseDTO) obj;
        return isAuthenticated == that.isAuthenticated &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(accessToken, that.accessToken) &&
                Objects.equals(refreshToken, that.refreshToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, accessToken, refreshToken, isAuthenticated);
    }

    @Override
    public String toString() {
        return "LoginResponseDTO{" +
                "userId=" + userId +
                ", accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", isAuthenticated=" + isAuthenticated +
                '}';
    }

    public static class Builder {
        private Long userId;
        private String accessToken;
        private String refreshToken;
        private boolean isAuthenticated;

        private Builder() {}

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public Builder isAuthenticated(boolean isAuthenticated) {
            this.isAuthenticated = isAuthenticated;
            return this;
        }

        public LoginResponseDTO build() {
            return new LoginResponseDTO(this);
        }
    }
}
