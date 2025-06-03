package com.example.supportorganizationsapp.dto.response;


import java.util.Objects;

public class ApiResponseDTO {
    private final String message;
    private final boolean status;

    private ApiResponseDTO(Builder builder) {
        this.message = builder.message;
        this.status = builder.status;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ApiResponseDTO that = (ApiResponseDTO) obj;
        return status == that.status && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, status);
    }

    @Override
    public String toString() {
        return "ApiResponseDTO{" +
                "message='" + message + '\'' +
                ", status=" + status +
                '}';
    }

    public static class Builder {
        private String message;
        private boolean status;

        private Builder() {}

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder status(boolean status) {
            this.status = status;
            return this;
        }

        public ApiResponseDTO build() {
            return new ApiResponseDTO(this);
        }
    }
}

