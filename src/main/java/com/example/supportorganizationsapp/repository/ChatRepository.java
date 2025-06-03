package com.example.supportorganizationsapp.repository;

import com.example.supportorganizationsapp.models.Chat;
import com.example.supportorganizationsapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("select c from chat c join c.users u where u.id = :userId")
    List<Chat> findChatByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM chat c WHERE c.isGroup = false AND :user2 MEMBER OF c.users AND :reqUser MEMBER OF c.users")
    Optional<Chat> findSingleChatByUsers(@Param("user2") User user2, @Param("reqUser") User reqUser);

    @Query("SELECT c FROM chat c WHERE c.isGroup = true AND LOWER(c.chatName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Chat> findChatsByNameContaining(@Param("name") String name);

    @Query("SELECT c FROM chat c WHERE :user MEMBER OF c.users AND LOWER(c.chatName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Chat> findChatsByNameContainingAndUser(@Param("name") String name, @Param("user") User user);

    @Query("SELECT c FROM chat c WHERE :user1 MEMBER OF c.users AND :user2 MEMBER OF c.users")
    List<Chat> findChatsByTwoUsers(@Param("user1") User user1, @Param("user2") User user2);

    @Query("SELECT c FROM chat c WHERE :targetUser MEMBER OF c.users AND :requestingUser MEMBER OF c.users")
    List<Chat> findChatsWithUserByRequestingUser(@Param("targetUser") User targetUser, @Param("requestingUser") User requestingUser);

}
