package com.example.math_race.race;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RaceAccount {
    private String id;
    private String sessionActive;
    private String joinToken;
    private String nickname;


    public boolean isGuest() {
        return id != null && id.startsWith("Guest-");
    }

    public boolean isConnected() {
        return sessionActive != null && !sessionActive.isEmpty();
    }

    public boolean containsJoinToken(){
        return joinToken != null && !joinToken.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        RaceAccount that = (RaceAccount) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
