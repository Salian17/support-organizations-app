package com.example.supportorganizationsapp.controllers;

import com.example.supportorganizationsapp.config.JwtConstants;
import com.example.supportorganizationsapp.dto.request.GroupChatRequestDTO;
import com.example.supportorganizationsapp.dto.request.UserIdRequest;
import com.example.supportorganizationsapp.dto.response.ApiResponseDTO;
import com.example.supportorganizationsapp.dto.response.ChatDTO;
import com.example.supportorganizationsapp.exception.ChatException;
import com.example.supportorganizationsapp.exception.UserException;
import com.example.supportorganizationsapp.models.Chat;
import com.example.supportorganizationsapp.models.User;
import com.example.supportorganizationsapp.service.ChatService;
import com.example.supportorganizationsapp.service.UserService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
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

    @PostMapping("/single")
    public ResponseEntity<ChatDTO> createSingleChat(
            @RequestBody UserIdRequest request,
            @RequestHeader(JwtConstants.TOKEN_HEADER) String jwt)
            throws UserException {

        User user = userService.findUserByProfile(jwt);
        Chat chat = chatService.createChat(user, request.getUserId());
        log.info("User {} created single chat: {}", user.getEmail(), chat.getId());

        return new ResponseEntity<>(ChatDTO.fromChat(chat), HttpStatus.OK);
    }

    @PostMapping("/group")
    public ResponseEntity<ChatDTO> createGroupChat(@RequestBody GroupChatRequestDTO req,
                                                   @RequestHeader(JwtConstants.TOKEN_HEADER) String jwt)
            throws UserException {

        User user = userService.findUserByProfile(jwt);
        Chat chat = chatService.createGroup(req, user);

        log.info("User {} created group chat: {}", user.getEmail(), chat.getId());

        return new ResponseEntity<>(ChatDTO.fromChat(chat), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatDTO> findChatById(@PathVariable("id") Long id)
            throws ChatException {

        Chat chat = chatService.findChatById(id);

        return new ResponseEntity<>(ChatDTO.fromChat(chat), HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<List<ChatDTO>> findAllChatsByUserId(@RequestHeader(JwtConstants.TOKEN_HEADER) String jwt)
            throws UserException {

        User user = userService.findUserByProfile(jwt);
        List<Chat> chats = chatService.findAllByUserId(user.getId());

        return new ResponseEntity<>(ChatDTO.fromChats(chats), HttpStatus.OK);
    }

    @PutMapping("/{chatId}/add/{userId}")
    public ResponseEntity<ChatDTO> addUserToGroup(@PathVariable Long chatId, @PathVariable Long userId,
                                                  @RequestHeader(JwtConstants.TOKEN_HEADER) String jwt)
            throws UserException, ChatException {

        User user = userService.findUserByProfile(jwt);
        Chat chat = chatService.addUserToGroup(userId, chatId, user);
        log.info("User {} added user {} to group chat: {}", user.getEmail(), userId, chat.getId());

        return new ResponseEntity<>(ChatDTO.fromChat(chat), HttpStatus.OK);
    }

    @PutMapping("/{chatId}/remove/{userId}")
    public ResponseEntity<ChatDTO> removeUserFromGroup(@PathVariable Long chatId, @PathVariable Long userId,
                                                       @RequestHeader(JwtConstants.TOKEN_HEADER) String jwt)
            throws UserException, ChatException {

        User user = userService.findUserByProfile(jwt);
        Chat chat = chatService.removeFromGroup(chatId, userId, user);
        log.info("User {} removed user {} from group chat: {}", user.getEmail(), userId, chat.getId());

        return new ResponseEntity<>(ChatDTO.fromChat(chat), HttpStatus.OK);
    }

    @PutMapping("/{chatId}/markAsRead")
    public ResponseEntity<ChatDTO> markAsRead(@PathVariable Long chatId,
                                              @RequestHeader(JwtConstants.TOKEN_HEADER) String jwt)
            throws UserException, ChatException {

        User user = userService.findUserByProfile(jwt);
        Chat chat = chatService.markAsRead(chatId, user);
        log.info("Chat {} marked as read for user: {}", chatId, user.getEmail());

        return new ResponseEntity<>(ChatDTO.fromChat(chat), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponseDTO> deleteChat(@PathVariable Long id,
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

}
