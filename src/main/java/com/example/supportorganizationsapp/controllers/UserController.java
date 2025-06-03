package com.example.supportorganizationsapp.controllers;

import com.example.supportorganizationsapp.config.JwtConstants;
import com.example.supportorganizationsapp.dto.request.UpdateUserRequestDTO;
import com.example.supportorganizationsapp.dto.request.application.CreateApplicationRequest;
import com.example.supportorganizationsapp.dto.response.ApiResponseDTO;
import com.example.supportorganizationsapp.dto.response.UserDTO;
import com.example.supportorganizationsapp.dto.response.application.ApplicationResponse;
import com.example.supportorganizationsapp.enums.RoleEnum;
import com.example.supportorganizationsapp.exception.UserException;
import com.example.supportorganizationsapp.models.User;
import com.example.supportorganizationsapp.service.ApplicationService;
import com.example.supportorganizationsapp.service.UserService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
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
@OpenAPIDefinition(
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
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

    @Operation(
            summary = "Поиск пользователей по имени",
            description = "Возвращает список пользователей, чьё имя содержит указанную строку.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список найденных пользователей"),
                    @ApiResponse(responseCode = "400", description = "Некорректный параметр поиска")
            }
    )
    @GetMapping("/search")
    public ResponseEntity<Set<UserDTO>> searchUsersByName(@RequestParam("name") String name) {
        List<User> users = userService.searchUserByName(name);
        return new ResponseEntity<>(UserDTO.fromUsers(users), HttpStatus.OK);
    }
    @Operation(
            summary = "Найти пользователя по email",
            description = "Возвращает пользователя по email адресу.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь найден"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            }
    )
    @GetMapping("/by-email")
    public ResponseEntity<UserDTO> getUserByEmail(
            @Parameter(description = "Email адрес", required = true)
            @RequestParam("email") String email) throws UserException {
        User user = userService.findUserByEmail(email);
        return new ResponseEntity<>(UserDTO.fromUser(user), HttpStatus.OK);
    }

    @Operation(
            summary = "Получить всех пользователей",
            description = "Возвращает список всех пользователей в системе.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список пользователей"),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
            }
    )
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(UserDTO.fromUsersAsList(users), HttpStatus.OK);
    }

    @Operation(
            summary = "Получить пользователей по роли",
            description = "Возвращает список пользователей с указанной ролью.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список пользователей"),
                    @ApiResponse(responseCode = "400", description = "Некорректная роль")
            }
    )
    @GetMapping("/by-role")
    public ResponseEntity<List<UserDTO>> getUsersByRole(
            @Parameter(description = "Роль пользователя (PASSENGER или COMPANION)", required = true)
            @RequestParam("role") String role) {
        try {
            RoleEnum roleEnum = RoleEnum.valueOf(role.toUpperCase());
            List<User> users = userService.getUsersByRole(roleEnum);
            return new ResponseEntity<>(UserDTO.fromUsersAsList(users), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя по его ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь удален"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO> deleteUser(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long id) throws UserException {
        userService.deleteUser(id);
        log.info("User deleted with id: {}", id);
        ApiResponseDTO response = ApiResponseDTO.builder()
                .message("User deleted successfully")
                .status(true)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Найти пользователя по номеру телефона",
            description = "Возвращает пользователя по номеру телефона.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь найден"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            }
    )
    @GetMapping("/by-phone")
    public ResponseEntity<UserDTO> getUserByPhoneNumber(
            @Parameter(description = "Номер телефона", required = true)
            @RequestParam("phone") String phoneNumber) throws UserException {
        User user = userService.findUserByPhoneNumber(phoneNumber);
        return new ResponseEntity<>(UserDTO.fromUser(user), HttpStatus.OK);
    }

    @Operation(
            summary = "Найти пользователей по полному имени",
            description = "Возвращает список пользователей с указанными именем и фамилией.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список пользователей"),
                    @ApiResponse(responseCode = "400", description = "Некорректные параметры")
            }
    )
    @GetMapping("/by-full-name")
    public ResponseEntity<List<UserDTO>> getUsersByFullName(
            @Parameter(description = "Имя", required = true)
            @RequestParam("firstName") String firstName,
            @Parameter(description = "Фамилия", required = true)
            @RequestParam("lastName") String lastName) {
        List<User> users = userService.findUsersByFullName(firstName, lastName);
        return new ResponseEntity<>(UserDTO.fromUsersAsList(users), HttpStatus.OK);
    }
}
