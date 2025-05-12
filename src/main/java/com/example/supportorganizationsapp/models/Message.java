package com.example.supportorganizationsapp.models;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(name = "message")
@Builder
public class Message extends Base {

    private String content;
    private LocalDateTime timeStamp;
    private User user;
    private Chat chat;
    private Set<Long> readBy = new HashSet<>();

    public Message() {
    }

    public Message(String content, LocalDateTime timeStamp, User user, Chat chat, Set<Long> readBy) {
        this.content = content;
        this.timeStamp = timeStamp;
        this.user = user;
        this.chat = chat;
        this.readBy = readBy;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    @ManyToOne
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne
    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    @ElementCollection
    public Set<Long> getReadBy() {
        return readBy;
    }

    public void setReadBy(Set<Long> readBy) {
        this.readBy = readBy;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Message other)) {
            return false;
        }
        return Objects.equals(content, other.getContent())
                && Objects.equals(timeStamp, other.getTimeStamp())
                && Objects.equals(user, other.getUser())
                && Objects.equals(chat, other.getChat());
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, timeStamp, user, chat);
    }

//    @Override
//    public String toString() {
//        return "Message{" +
//                "id=" + id +
//                ", content='" + content + '\'' +
//                ", timeStamp=" + timeStamp +
//                '}';
//    }

}
