package com.example.supportorganizationsapp.controllers;

import com.example.supportorganizationsapp.config.JwtConstants;
import com.example.supportorganizationsapp.config.TokenProvider;
import com.example.supportorganizationsapp.dto.request.SendMessageRequestDTO;
import com.example.supportorganizationsapp.dto.request.UpdateMessageContentRequestDTO;
import com.example.supportorganizationsapp.dto.response.ApiResponseDTO;
import com.example.supportorganizationsapp.dto.response.MessageDTO;
import com.example.supportorganizationsapp.exception.ChatException;
import com.example.supportorganizationsapp.exception.MessageException;
import com.example.supportorganizationsapp.exception.UserException;
import com.example.supportorganizationsapp.models.Message;
import com.example.supportorganizationsapp.models.User;
import com.example.supportorganizationsapp.service.MessageService;
import com.example.supportorganizationsapp.service.UserService;
import io.jsonwebtoken.Claims;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
@OpenAPIDefinition(
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class MessageController {

    private final UserService userService;
    private final MessageService messageService;
    private final TokenProvider tokenProvider;

    @PostMapping("/create")
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody SendMessageRequestDTO req,
                                                  @RequestHeader(JwtConstants.TOKEN_HEADER) String jwt)
            throws ChatException, UserException {

        User user = userService.findUserByProfile(jwt);
        Message message = messageService.sendMessage(req, user.getId());
        log.info("User {} sent message: {}", user.getEmail(), message.getId());

        return new ResponseEntity<>(MessageDTO.fromMessage(message), HttpStatus.OK);
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<List<MessageDTO>> getChatMessages(@PathVariable Long chatId,
                                                         @RequestHeader(JwtConstants.TOKEN_HEADER) String jwt)
            throws ChatException, UserException {

        User user = userService.findUserByProfile(jwt);
        List<Message> messages = messageService.getChatMessages(chatId, user);

        return new ResponseEntity<>(MessageDTO.fromMessages(messages), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO> deleteMessage(@PathVariable Long id,
                                                        @RequestHeader(JwtConstants.TOKEN_HEADER) String jwt)
            throws UserException, MessageException {

        User user = userService.findUserByProfile(jwt);
        messageService.deleteMessageById(id, user);
        log.info("User {} deleted message: {}", user.getEmail(), id);

        ApiResponseDTO res = ApiResponseDTO.builder()
                .message("Message deleted successfully")
                .status(true)
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
    @Operation(
            summary = "Редактирование содержимого сообщения",
            description = "Позволяет автору сообщения изменить его содержимое",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Сообщение успешно обновлено"),
                    @ApiResponse(responseCode = "403", description = "Пользователь не авторизован для редактирования этого сообщения"),
                    @ApiResponse(responseCode = "404", description = "Сообщение не найдено")
            }
    )
    @PutMapping("/{messageId}/content")
    public ResponseEntity<MessageDTO> updateMessageContent(
            @Parameter(description = "ID сообщения", required = true)
            @PathVariable Long messageId,
            @Parameter(description = "Новое содержимое сообщения", required = true)
            @RequestBody UpdateMessageContentRequestDTO request,
            @Parameter(hidden = true) @RequestHeader("Authorization") String jwt) throws UserException, MessageException {

        Claims claims = tokenProvider.getClaimsFromToken(jwt);
        String email = claims.get(JwtConstants.EMAIL, String.class);
        User reqUser = userService.findUserByEmail(email);

        Message updatedMessage = messageService.updateMessageContent(messageId, request.getContent(), reqUser.getId());
        MessageDTO messageDTO = MessageDTO.fromMessage(updatedMessage);

        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }

    @Operation(
            summary = "Отметка сообщения как прочитанное",
            description = "Добавляет пользователя в список прочитавших сообщение",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Сообщение отмечено как прочитанное"),
                    @ApiResponse(responseCode = "403", description = "Пользователь не является участником чата"),
                    @ApiResponse(responseCode = "404", description = "Сообщение не найдено")
            }
    )
    @PostMapping("/{messageId}/read")
    public ResponseEntity<MessageDTO> markMessageAsRead(
            @Parameter(description = "ID сообщения", required = true)
            @PathVariable Long messageId,
            @Parameter(hidden = true) @RequestHeader("Authorization") String jwt) throws UserException, MessageException {

        Claims claims = tokenProvider.getClaimsFromToken(jwt);
        String email = claims.get(JwtConstants.EMAIL, String.class);
        User reqUser = userService.findUserByEmail(email);

        Message message = messageService.markMessageAsRead(messageId, reqUser.getId());
        MessageDTO messageDTO = MessageDTO.fromMessage(message);

        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }

    @Operation(
            summary = "Поиск сообщений по содержимому",
            description = "Ищет сообщения в указанном чате по содержимому (регистронезависимый поиск)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Результаты поиска получены"),
                    @ApiResponse(responseCode = "403", description = "Пользователь не является участником чата"),
                    @ApiResponse(responseCode = "404", description = "Чат не найден")
            }
    )
    @GetMapping("/search")
    public ResponseEntity<List<MessageDTO>> searchMessagesByContent(
            @Parameter(description = "ID чата для поиска", required = true)
            @RequestParam Long chatId,
            @Parameter(description = "Текст для поиска", required = true)
            @RequestParam String searchText,
            @Parameter(hidden = true) @RequestHeader("Authorization") String jwt) throws UserException, ChatException {

        Claims claims = tokenProvider.getClaimsFromToken(jwt);
        String email = claims.get(JwtConstants.EMAIL, String.class);
        User reqUser = userService.findUserByEmail(email);

        List<Message> messages = messageService.searchMessagesByContent(searchText, chatId, reqUser.getId());
        List<MessageDTO> messageDTOs = MessageDTO.fromMessages(messages);

        return new ResponseEntity<>(messageDTOs, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение последнего сообщения от пользователя",
            description = "Возвращает последнее сообщение от указанного пользователя в чате",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Последнее сообщение найдено"),
                    @ApiResponse(responseCode = "403", description = "Пользователь не является участником чата"),
                    @ApiResponse(responseCode = "404", description = "Сообщения от указанного пользователя не найдены")
            }
    )
    @GetMapping("/last")
    public ResponseEntity<MessageDTO> getLastMessageFromUser(
            @Parameter(description = "ID пользователя, чьё последнее сообщение нужно найти", required = true)
            @RequestParam Long userId,
            @Parameter(description = "ID чата для поиска", required = true)
            @RequestParam Long chatId,
            @Parameter(hidden = true) @RequestHeader("Authorization") String jwt) throws UserException, ChatException, MessageException {

        Claims claims = tokenProvider.getClaimsFromToken(jwt);
        String email = claims.get(JwtConstants.EMAIL, String.class);
        User reqUser = userService.findUserByEmail(email);

        Message message = messageService.getLastMessageFromUser(userId, chatId, reqUser.getId());
        MessageDTO messageDTO = MessageDTO.fromMessage(message);

        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }
}
