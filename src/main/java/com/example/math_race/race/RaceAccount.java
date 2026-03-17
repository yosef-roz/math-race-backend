package com.example.math_race.race;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RaceAccount {
    private String id;
    private String sessionActive;
    private String nickname;


    public boolean isGuest() {
        return id != null && id.startsWith("Guest-");
    }

    public boolean isConnected() {
        return sessionActive != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        RaceAccount that = (RaceAccount) o;
        return Objects.equals(id, that.id);
    }
}
