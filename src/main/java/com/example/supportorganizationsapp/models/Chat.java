package com.example.supportorganizationsapp.models;

import jakarta.persistence.*;
import lombok.Builder;

import java.util.*;

@Entity(name = "chat")
@Builder
public class Chat extends Base {

    private String chatName;
    private Boolean isGroup;
    private Set<User> admins = new HashSet<>();
    private Set<User> users = new HashSet<>();
    private User createdBy;
    private List<Message> messages = new ArrayList<>();

    public Chat() {
    }

    public Chat(String chatName, Boolean isGroup, Set<User> admins, Set<User> users, User createdBy, List<Message> messages) {
        this.chatName = chatName;
        this.isGroup = isGroup;
        this.admins = admins;
        this.users = users;
        this.createdBy = createdBy;
        this.messages = messages;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public Boolean getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(Boolean group) {
        isGroup = group;
    }

    @ManyToMany
    public Set<User> getAdmins() {
        return admins;
    }

    public void setAdmins(Set<User> admins) {
        this.admins = admins;
    }

    @ManyToMany
    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @ManyToOne
    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    @OneToMany(cascade = CascadeType.ALL)
    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Chat other)) {
            return false;
        }
        return Objects.equals(chatName, other.getChatName())
                && Objects.equals(isGroup, other.getIsGroup())
                && Objects.equals(admins, other.getAdmins())
                && Objects.equals(users, other.getUsers())
                && Objects.equals(createdBy, other.getCreatedBy());
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatName, isGroup, admins, users, createdBy);
    }

//    @Override
//    public String toString() {
//        return "Chat{" +
//                "id=" + id +
//                ", chatName='" + chatName + '\'' +
//                ", isGroup=" + isGroup +
//                '}';
//    }

}
