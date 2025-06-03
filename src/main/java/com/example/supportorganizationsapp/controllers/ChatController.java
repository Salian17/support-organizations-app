package com.example.supportorganizationsapp.controllers;

import com.example.supportorganizationsapp.config.JwtConstants;
import com.example.supportorganizationsapp.dto.request.GroupChatRequestDTO;
import com.example.supportorganizationsapp.dto.request.UserIdRequest;
import com.example.supportorganizationsapp.dto.response.ApiResponseDTO;
import com.example.supportorganizationsapp.dto.response.ChatDTO;
import com.example.supportorganizationsapp.dto.response.UserDTO;
import com.example.supportorganizationsapp.exception.ChatException;
import com.example.supportorganizationsapp.exception.UserException;
import com.example.supportorganizationsapp.models.Chat;
import com.example.supportorganizationsapp.models.User;
import com.example.supportorganizationsapp.service.ChatService;
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
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chats")
@OpenAPIDefinition(
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class ChatController {

    private final UserService userService;
    private final ChatService chatService;

    @Operation(
            summary = "Создание приватного чата",
            description = "Создаёт новый приватный чат между двумя пользователями",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Чат успешно создан"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные пользователя"),
                    @ApiResponse(responseCode = "401", description = "Неавторизованный доступ")
            }
    )
    @PostMapping("/single")
    public ResponseEntity<ChatDTO> createSingleChat(
            @Parameter(description = "Данные для создания приватного чата", required = true)
            @RequestBody UserIdRequest request,
            @RequestHeader(JwtConstants.TOKEN_HEADER) String jwt)
            throws UserException {

        User user = userService.findUserByProfile(jwt);
        Chat chat = chatService.createChat(user, request.getUserId());
        log.info("User {} created single chat: {}", user.getEmail(), chat.getId());

        return new ResponseEntity<>(ChatDTO.fromChat(chat), HttpStatus.OK);
    }

    @Operation(
            summary = "Создание группового чата",
            description = "Создаёт новый групповой чат с указанными участниками",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Групповой чат успешно создан"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные группы"),
                    @ApiResponse(responseCode = "401", description = "Неавторизованный доступ")
            }
    )
    @PostMapping("/group")
    public ResponseEntity<ChatDTO> createGroupChat(
            @Parameter(description = "Данные для создания группового чата", required = true)
            @RequestBody GroupChatRequestDTO req,
            @RequestHeader(JwtConstants.TOKEN_HEADER) String jwt)
            throws UserException {

        User user = userService.findUserByProfile(jwt);
        Chat chat = chatService.createGroup(req, user);

        log.info("User {} created group chat: {}", user.getEmail(), chat.getId());

        return new ResponseEntity<>(ChatDTO.fromChat(chat), HttpStatus.OK);
    }

    @Operation(
            summary = "Получение чата по ID",
            description = "Возвращает информацию о чате по его идентификатору",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Чат найден"),
                    @ApiResponse(responseCode = "404", description = "Чат не найден")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ChatDTO> findChatById(
            @Parameter(description = "ID чата", required = true)
            @PathVariable("id") Long id)
            throws ChatException {

        Chat chat = chatService.findChatById(id);

        return new ResponseEntity<>(ChatDTO.fromChat(chat), HttpStatus.OK);
    }

    @Operation(
            summary = "Получение всех чатов пользователя",
            description = "Возвращает список всех чатов, в которых участвует пользователь",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список чатов пользователя"),
                    @ApiResponse(responseCode = "401", description = "Неавторизованный доступ")
            }
    )
    @GetMapping("/user")
    public ResponseEntity<List<ChatDTO>> findAllChatsByUserId(
            @RequestHeader(JwtConstants.TOKEN_HEADER) String jwt)
            throws UserException {

        User user = userService.findUserByProfile(jwt);
        List<Chat> chats = chatService.findAllByUserId(user.getId());

        return new ResponseEntity<>(ChatDTO.fromChats(chats), HttpStatus.OK);
    }

    @Operation(
            summary = "Добавление пользователя в группу",
            description = "Добавляет указанного пользователя в групповой чат",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь успешно добавлен в группу"),
                    @ApiResponse(responseCode = "404", description = "Чат или пользователь не найдены"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав для добавления в группу")
            }
    )
    @PutMapping("/{chatId}/add/{userId}")
    public ResponseEntity<ChatDTO> addUserToGroup(
            @Parameter(description = "ID чата", required = true)
            @PathVariable Long chatId,
            @Parameter(description = "ID добавляемого пользователя", required = true)
            @PathVariable Long userId,
            @RequestHeader(JwtConstants.TOKEN_HEADER) String jwt)
            throws UserException, ChatException {

        User user = userService.findUserByProfile(jwt);
        Chat chat = chatService.addUserToGroup(userId, chatId, user);
        log.info("User {} added user {} to group chat: {}", user.getEmail(), userId, chat.getId());

        return new ResponseEntity<>(ChatDTO.fromChat(chat), HttpStatus.OK);
    }

    @Operation(
            summary = "Удаление пользователя из группы",
            description = "Удаляет указанного пользователя из группового чата",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь успешно удален из группы"),
                    @ApiResponse(responseCode = "404", description = "Чат или пользователь не найдены"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав для удаления из группы")
            }
    )
    @PutMapping("/{chatId}/remove/{userId}")
    public ResponseEntity<ChatDTO> removeUserFromGroup(
            @Parameter(description = "ID чата", required = true)
            @PathVariable Long chatId,
            @Parameter(description = "ID удаляемого пользователя", required = true)
            @PathVariable Long userId,
            @RequestHeader(JwtConstants.TOKEN_HEADER) String jwt)
            throws UserException, ChatException {

        User user = userService.findUserByProfile(jwt);
        Chat chat = chatService.removeFromGroup(chatId, userId, user);
        log.info("User {} removed user {} from group chat: {}", user.getEmail(), userId, chat.getId());

        return new ResponseEntity<>(ChatDTO.fromChat(chat), HttpStatus.OK);
    }

    @Operation(
            summary = "Отметка чата как прочитанного",
            description = "Помечает чат как прочитанный для текущего пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Чат отмечен как прочитанный"),
                    @ApiResponse(responseCode = "404", description = "Чат не найден"),
                    @ApiResponse(responseCode = "401", description = "Неавторизованный доступ")
            }
    )
    @PutMapping("/{chatId}/markAsRead")
    public ResponseEntity<ChatDTO> markAsRead(
            @Parameter(description = "ID чата", required = true)
            @PathVariable Long chatId,
            @RequestHeader(JwtConstants.TOKEN_HEADER) String jwt)
            throws UserException, ChatException {

        User user = userService.findUserByProfile(jwt);
        Chat chat = chatService.markAsRead(chatId, user);
        log.info("Chat {} marked as read for user: {}", chatId, user.getEmail());

        return new ResponseEntity<>(ChatDTO.fromChat(chat), HttpStatus.OK);
    }

    @Operation(
            summary = "Удаление чата",
            description = "Удаляет чат по его ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Чат успешно удален"),
                    @ApiResponse(responseCode = "404", description = "Чат не найден"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав для удаления чата")
            }
    )
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponseDTO> deleteChat(
            @Parameter(description = "ID чата", required = true)
            @PathVariable Long id,
            @RequestHeader(JwtConstants.TOKEN_HEADER) String jwt)
            throws UserException, ChatException {

        User user = userService.findUserByProfile(jwt);
        chatService.deleteChat(id, user.getId());
        log.info("User {} deleted chat: {}", user.getEmail(), id);

        ApiResponseDTO res = ApiResponseDTO.builder()
                .message("Chat deleted successfully")
                .status(true)
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(
            summary = "Назначение администратора",
            description = "Назначает пользователя администратором группового чата",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь назначен администратором"),
                    @ApiResponse(responseCode = "404", description = "Чат или пользователь не найдены"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав для назначения администратора")
            }
    )
    @PutMapping("/{chatId}/promote/{userId}")
    public ResponseEntity<ChatDTO> promoteToAdmin(
            @Parameter(description = "ID чата", required = true)
            @PathVariable Long chatId,
            @Parameter(description = "ID пользователя для назначения администратором", required = true)
            @PathVariable Long userId,
            @RequestHeader(JwtConstants.TOKEN_HEADER) String jwt)
            throws UserException, ChatException {

        User user = userService.findUserByProfile(jwt);
        Chat chat = chatService.promoteToAdmin(chatId, userId, user);
        log.info("User {} promoted user {} to admin in chat: {}", user.getEmail(), userId, chatId);

        return new ResponseEntity<>(ChatDTO.fromChat(chat), HttpStatus.OK);
    }

    @Operation(
            summary = "Передача владения чатом",
            description = "Передаёт право владения чатом другому пользователю",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Владение чатом успешно передано"),
                    @ApiResponse(responseCode = "404", description = "Чат или пользователь не найдены"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав для передачи владения")
            }
    )
    @PutMapping("/{chatId}/transfer-ownership/{newOwnerId}")
    public ResponseEntity<ChatDTO> transferOwnership(
            @Parameter(description = "ID чата", required = true)
            @PathVariable Long chatId,
            @Parameter(description = "ID нового владельца", required = true)
            @PathVariable Long newOwnerId,
            @RequestHeader(JwtConstants.TOKEN_HEADER) String jwt)
            throws UserException, ChatException {

        User user = userService.findUserByProfile(jwt);
        Chat chat = chatService.transferOwnership(chatId, newOwnerId, user);
        log.info("User {} transferred ownership of chat {} to user {}", user.getEmail(), chatId, newOwnerId);

        return new ResponseEntity<>(ChatDTO.fromChat(chat), HttpStatus.OK);
    }

    @Operation(
            summary = "Получение списка администраторов чата",
            description = "Возвращает список всех администраторов указанного чата",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список администраторов чата"),
                    @ApiResponse(responseCode = "404", description = "Чат не найден"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав для просмотра администраторов")
            }
    )
    @GetMapping("/{chatId}/admins")
    public ResponseEntity<Set<UserDTO>> getChatAdmins(
            @Parameter(description = "ID чата", required = true)
            @PathVariable Long chatId,
            @RequestHeader(JwtConstants.TOKEN_HEADER) String jwt)
            throws ChatException, UserException {

        User user = userService.findUserByProfile(jwt);
        Set<User> admins = chatService.getChatAdmins(chatId, user);

        Set<UserDTO> adminDTOs = admins.stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toSet());

        log.info("User {} requested admins list for chat: {}", user.getEmail(), chatId);

        return new ResponseEntity<>(adminDTOs, HttpStatus.OK);
    }

    @Operation(
            summary = "Поиск чатов по названию",
            description = "Ищет чаты пользователя по названию",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список найденных чатов"),
                    @ApiResponse(responseCode = "401", description = "Неавторизованный доступ")
            }
    )
    @GetMapping("/search")
    public ResponseEntity<List<ChatDTO>> searchChatsByName(
            @Parameter(description = "Название для поиска", required = true)
            @RequestParam String name,
            @RequestHeader(JwtConstants.TOKEN_HEADER) String jwt)
            throws UserException {

        User user = userService.findUserByProfile(jwt);
        List<Chat> chats = chatService.searchChatsByName(name, user);

        log.info("User {} searched chats by name: {}", user.getEmail(), name);

        return new ResponseEntity<>(ChatDTO.fromChats(chats), HttpStatus.OK);
    }

    @Operation(
            summary = "Поиск чатов с определенным пользователем",
            description = "Возвращает список чатов, в которых участвуют текущий пользователь и указанный пользователь",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список чатов с пользователем"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
                    @ApiResponse(responseCode = "401", description = "Неавторизованный доступ")
            }
    )
    @GetMapping("/with-user/{userId}")
    public ResponseEntity<List<ChatDTO>> findChatsWithUser(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long userId,
            @RequestHeader(JwtConstants.TOKEN_HEADER) String jwt)
            throws UserException {

        User user = userService.findUserByProfile(jwt);
        List<Chat> chats = chatService.findChatsWithUser(userId, user);

        log.info("User {} searched chats with user: {}", user.getEmail(), userId);

        return new ResponseEntity<>(ChatDTO.fromChats(chats), HttpStatus.OK);
    }
}