package com.example.supportorganizationsapp.config;

import com.example.supportorganizationsapp.enums.RoleEnum;
import com.example.supportorganizationsapp.enums.StatusEnum;
import com.example.supportorganizationsapp.models.Application;
import com.example.supportorganizationsapp.models.Chat;
import com.example.supportorganizationsapp.models.Message;
import com.example.supportorganizationsapp.models.User;
import com.example.supportorganizationsapp.repository.ApplicationRepository;
import com.example.supportorganizationsapp.repository.ChatRepository;
import com.example.supportorganizationsapp.repository.MessageRepository;
import com.example.supportorganizationsapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    @Bean
    @Transactional
    public CommandLineRunner initData() {
        return args -> {
            User passenger = User.builder()
                    .email("passenger@example.com")
                    .phoneNumber("+79001112233")
                    .firstName("Иван")
                    .lastName("Иванов")
                    .password("pass123")
                    .roleEnum(RoleEnum.PASSENGER)
                    .build();

            User companion = User.builder()
                    .email("companion@example.com")
                    .phoneNumber("+79004445566")
                    .firstName("Петр")
                    .lastName("Петров")
                    .password("comp456")
                    .roleEnum(RoleEnum.COMPANION)
                    .build();

            userRepository.saveAll(List.of(passenger, companion));

            Application app1 = Application.builder()
                    .date("2025-05-20")
                    .time("10:00")
                    .departureStation("Новослабодская")
                    .destinationStation("Менделеевская")
                    .comment("Просьба приехать вовремя")
                    .status(StatusEnum.NEW)
                    .passenger(passenger)
                    .build();

            Application app2 = Application.builder()
                    .date("2025-05-22")
                    .time("14:30")
                    .departureStation("Новослабодская")
                    .destinationStation("Проспект мира")
                    .comment("Нужна помощь с коляской")
                    .status(StatusEnum.INPROGRESS)
                    .passenger(passenger)
                    .companion(companion)
                    .build();

            applicationRepository.saveAll(List.of(app1, app2));

            Chat chat = Chat.builder()
                    .chatName("Сопровождение Иванова")
                    .isGroup(false)
                    .admins(Set.of(companion))
                    .users(Set.of(passenger, companion))
                    .createdBy(companion)
                    .build();

            chatRepository.save(chat);

            Message message = Message.builder()
                    .content("Здравствуйте! Я вас буду сопровождать.")
                    .timeStamp(LocalDateTime.now())
                    .user(companion)
                    .chat(chat)
                    .readBy(Set.of(passenger.getId()))
                    .build();

            messageRepository.save(message);

            chat.setMessages(List.of(message));
            chatRepository.save(chat);
        };
    }
}
