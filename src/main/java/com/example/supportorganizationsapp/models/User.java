package com.example.supportorganizationsapp.models;

import com.example.supportorganizationsapp.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.Builder;

import java.util.List;
import java.util.Objects;

@Entity(name = "APP_USER")
@Builder
public class User extends Base {

    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String password;
    private RoleEnum roleEnum;
    private List<Application> applications;

    /**
     * Конструктор по умолчанию, необходимый для работы JPA.
     */
    public User() {
    }

    public User(String email, String phoneNumber, String firstName, String lastName, String password, RoleEnum roleEnum, List<Application> applications) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.roleEnum = roleEnum;
        this.applications = applications;
    }

    @Column(unique = true)
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @Enumerated(EnumType.STRING)
    public RoleEnum getRoleEnum() {
        return roleEnum;
    }
    public void setRoleEnum(RoleEnum roleEnum) {
        this.roleEnum = roleEnum;
    }

    @OneToMany(mappedBy = "passenger")
    public List<Application> getApplications() {
        return applications;
    }
    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof User other)) {
            return false;
        }
        return Objects.equals(email, other.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
