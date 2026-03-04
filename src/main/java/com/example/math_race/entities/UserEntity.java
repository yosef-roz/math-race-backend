package com.example.math_race.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserEntity extends BaseEntity {

    public enum UserRole {
        ADMIN,
        USER,
    }

    private String username;
    private String password;
    private String email;
    private UserRole role;
    private boolean verified;

    public UserEntity(String username, String password, String email) {
        super();
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = UserRole.USER;
        this.verified = false;
    }

    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }

    public boolean isUser() {
        return this.role == UserRole.USER;
    }
}