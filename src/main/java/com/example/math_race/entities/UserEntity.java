package com.example.math_race.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseEntity {
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
        this.role = UserRole.PLAYER;
        this.verified = false;
    }

    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }
}