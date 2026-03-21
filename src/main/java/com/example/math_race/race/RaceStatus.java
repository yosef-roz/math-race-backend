package com.example.math_race.race;

import lombok.Getter;

@Getter
public enum RaceStatus {

    PENDING(1, "Waiting for players"),

    IN_PROGRESS(2, "Race is active"),

    PAUSED(3, "Race paused by manager"),

    CANCELLED(4, "Race cancelled"),

    FINISHED(5, "Race finished");

    private final int id;
    private final String description;

    RaceStatus(int id, String description) {
        this.id = id;
        this.description = description;
    }


    public boolean isClosed() {
        return this == FINISHED || this == CANCELLED;
    }

    public boolean isOpen() {
        return !isClosed();
    }

    public boolean isRunning() {
        return this == IN_PROGRESS;
    }

}