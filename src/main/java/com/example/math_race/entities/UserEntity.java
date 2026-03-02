package com.example.math_race.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserEntity extends BaseEntity {
    private String username;
    private String password;
    private String email;
    private UserRole role;
    private boolean active;

    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }
}