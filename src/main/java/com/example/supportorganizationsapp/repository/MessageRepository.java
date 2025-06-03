package com.example.supportorganizationsapp.repository;

import com.example.supportorganizationsapp.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByChat_Id(Long chatId);
    @Query("SELECT m FROM message m WHERE m.chat.id = :chatId AND LOWER(m.content) LIKE LOWER(CONCAT('%', :searchText, '%')) ORDER BY m.timeStamp DESC")
    List<Message> findByChat_IdAndContentContainingIgnoreCase(@Param("chatId") Long chatId, @Param("searchText") String searchText);

    @Query("SELECT m FROM message m WHERE m.chat.id = :chatId AND m.user.id = :userId ORDER BY m.timeStamp DESC")
    List<Message> findByChat_IdAndUser_IdOrderByTimeStampDesc(@Param("chatId") Long chatId, @Param("userId") Long userId);
}
