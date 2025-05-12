package com.example.supportorganizationsapp.controllers;

import com.example.supportorganizationsapp.config.JwtConstants;
import com.example.supportorganizationsapp.dto.request.UpdateUserRequestDTO;
import com.example.supportorganizationsapp.dto.request.application.CreateApplicationRequest;
import com.example.supportorganizationsapp.dto.response.ApiResponseDTO;
import com.example.supportorganizationsapp.dto.response.UserDTO;
import com.example.supportorganizationsapp.dto.response.application.ApplicationResponse;
import com.example.supportorganizationsapp.exception.UserException;
import com.example.supportorganizationsapp.models.User;
import com.example.supportorganizationsapp.service.ApplicationService;
import com.example.supportorganizationsapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ApplicationService applicationService;

    @Operation(
            summary = "Получить информацию о текущем пользователе",
            description = "Возвращает данные пользователя, основываясь на текущем контексте безопасности.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение данных"),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
            }
    )
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile(@RequestHeader(JwtConstants.TOKEN_HEADER) String token) throws UserException {
        User user = userService.findUserByProfile(token);
        return new ResponseEntity<>(UserDTO.fromUser(user), HttpStatus.OK);
    }

    @Operation(
            summary = "Получить список заявок пользователя",
            description = "Возвращает список всех заявок пользователя по его ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список заявок"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            }
    )
    @GetMapping("/{userId}/applications")
    public ResponseEntity<List<ApplicationResponse>> getUserApplications(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long userId) throws UserException {
        List<ApplicationResponse> applications = userService.getUserApplications(userId);
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }

    @Operation(
            summary = "Создать заявку",
            description = "Создает новую заявку для пользователя.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Заявка успешно создана"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные")
            }
    )
    @PostMapping("/applications")
    public ResponseEntity<ApplicationResponse> createApplication(
            @Parameter(description = "Данные для создания заявки", required = true)
            @RequestBody CreateApplicationRequest applicationRequest) {
        ApplicationResponse response = applicationService.createApplication(applicationRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Отменить заявку",
            description = "Отменяет заявку по её ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Заявка отменена"),
                    @ApiResponse(responseCode = "404", description = "Заявка не найдена")
            }
    )
    @PostMapping("/applications/{id}/cancel")
    public ResponseEntity<ApplicationResponse> cancelApplication(
            @Parameter(description = "ID заявки", required = true)
            @PathVariable Long id) {
        ApplicationResponse response = applicationService.cancelApplication(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Редактировать профиль пользователя",
            description = "Обновляет данные пользователя.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Данные обновлены"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            }
    )
    @PutMapping("/update")
    public ResponseEntity<ApiResponseDTO> updateUser(
            @Parameter(description = "Данные для обновления")
            @RequestBody UpdateUserRequestDTO request,
            @Parameter(description = "Токен доступа")
            @RequestHeader(JwtConstants.TOKEN_HEADER) String token)
            throws UserException {
        User user = userService.findUserByProfile(token);
        user = userService.updateUser(user.getId(), request);
        log.info("User updated: {}", user.getEmail());
        ApiResponseDTO response = ApiResponseDTO.builder()
                .message("User updated")
                .status(true)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Set<UserDTO>> searchUsersByName(@RequestParam("name") String name) {
        List<User> users = userService.searchUserByName(name);
        return new ResponseEntity<>(UserDTO.fromUsers(users), HttpStatus.OK);
    }
}
