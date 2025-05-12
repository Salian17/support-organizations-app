package com.example.supportorganizationsapp.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Регистрация пользователя")
public class SignUpRequest {
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String password;

    private SignUpRequest() {}

    @Email
    @Schema(description = "Почта", example = "test@example.ru")
    @NotEmpty(message = "Почта не может быть пустая")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Schema(description = "Номер телефона", example = "79999990000")
    @NotEmpty(message = "Номер телефона не может быть пустой")
    @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{10,20}$", message = "Неверный формат номера телефона")
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Schema(description = "Имя", example = "Иван")
    @NotEmpty(message = "Имя не может быть пустое")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Schema(description = "Фамилия", example = "Иванов")
    @NotEmpty(message = "Фамилия не может быть пустая")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    @NotEmpty
    @Schema(description = "Пароль", example = "password")
    @Size(min = 6, message = "Пароль должен содержать не меньше 6 символов")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
