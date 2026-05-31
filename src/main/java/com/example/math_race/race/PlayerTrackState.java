package com.example.math_race.race;

import lombok.Getter;

@Getter
public enum PlayerTrackState {
    REGULAR("medium",30,30),                // שאלות רגילות
    WAITING_FOR_CHOICE(10,0),     // השחקן קיבל צומת וממתינים שיבחר
    AUTOSTRADA("hard",60,100,1),             // השחקן במסלול אוטוסטרדה (שאלה 1 קשה)
    DIRT_ROAD("easy",15,10,10);            // השחקן בשביל עפר (10 שאלות קלות)

    private final String level;
    private final int timeLimitSeconds;
    private final int score;
    private final Integer questionsNumber;

    PlayerTrackState(String level, int timeLimitSeconds, int score,Integer questionsNumber) {
        this.level = level;
        this.timeLimitSeconds = timeLimitSeconds;
        this.questionsNumber = questionsNumber;
        this.score = score;
    }

    PlayerTrackState(String level, int timeLimitSeconds, int score) {
     this(level, timeLimitSeconds, score, null);
    }

    PlayerTrackState(int timeLimitSeconds, int score) {
        this(null, timeLimitSeconds, score);
    }

    public long getTimeLimitMillis() {
        return timeLimitSeconds * 1000L;
    }

}
