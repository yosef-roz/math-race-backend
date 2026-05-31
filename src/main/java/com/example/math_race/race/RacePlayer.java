package com.example.math_race.race;

import com.example.math_race.entities.UserEntity;
import com.example.math_race.questionGenerator.question.MathQuestion;
import lombok.*;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class RacePlayer extends RaceAccount{
    private static final String[] CAR_COLORS = {
            "#3b82f6", // כחול
            "#22c55e", // ירוק
            "#ef4444", // אדום
            "#f59e0b", // כתום
            "#a855f7", // סגול
            "#ec4899", // ורוד
            "#06b6d4", // תכלת
            "#eab308", // צהוב
            "#14b8a6", // טורקיז
            "#6366f1"  // אינדיגו
    };

    private MathQuestion currentQuestion;
    private boolean canAskHint;
    private boolean gotHint;
    private int numHintsReceived;

    private long questionRemainingTimeMs;
    private long questionStartTimeAtMs;
    private int currentScore;
    private String carColor;

    private PlayerTrackState trackState;
    private int specialQuestionsRemaining;

    private int regularQAttempts;
    private int regularQSuccesses;
    private long regularQTimeMs;
    private int maxRegularStreak;
    private int currentRegularStreak;
    private long regularSuccessTimeMs;

    private int junctionsOfferedCount;
    private int autostradaChoices;
    private int dirtRoadChoices;
    private int lastJunctionRegularQCount;

    public RacePlayer(String accountId, String sessionActive, String joinToken ,String nickname){
        this(accountId,null,sessionActive,joinToken,nickname);
    }

    public RacePlayer(UserEntity user, String sessionActive, String joinToken , String nickname){
        this(user.getId()+"",user,sessionActive,joinToken,nickname);
    }

    public RacePlayer(String accountId, UserEntity user, String sessionActive, String joinToken ,String nickname){
        super(accountId,user,sessionActive,joinToken,nickname);
        this.carColor = assignRandomColor();
        this.currentScore = 0;
        this.regularQAttempts = 0;
        this.regularQSuccesses = 0;
        this.regularQTimeMs = 0;
        this.maxRegularStreak = 0;
        this.regularSuccessTimeMs = 0;
        this.currentRegularStreak = 0;
        this.junctionsOfferedCount = 0;
        this.autostradaChoices = 0;
        this.dirtRoadChoices = 0;
        this.lastJunctionRegularQCount = 0;
        this.numHintsReceived = 0;

        this.trackState = PlayerTrackState.REGULAR;
        this.specialQuestionsRemaining = 0;
    }

    public boolean checkAnswer(String answer){
        return currentQuestion != null && Objects.equals(currentQuestion.getCorrectAnswer(),answer);
    }

    public void addScore(int score){

        this.currentScore += score;
    }

    public long getCalculatedQuestionRemainingTime(RaceStatus currentRaceStatus) {
        if (currentRaceStatus != RaceStatus.IN_PROGRESS) {
            return this.questionRemainingTimeMs;
        }

        long timeElapsed = System.currentTimeMillis() - this.questionStartTimeAtMs;
        long actualRemaining = this.questionRemainingTimeMs - timeElapsed;

        return Math.max(0, actualRemaining);
    }

    public void addRegularAttempt() {
        this.regularQAttempts += 1;
    }

    public void addRegularSuccess() {
        this.regularQSuccesses += 1;
    }

    public void addRegularTimeMs(long timeMs) {
        this.regularQTimeMs += timeMs;
    }

    public void addRegularStreak(int streak) {
        this.currentRegularStreak += streak;
    }

    public void addRegularSuccessTimeMs(long timeMs) {
        this.regularSuccessTimeMs += timeMs;
    }

    public void addJunctionsOfferedCount() {
        this.junctionsOfferedCount += 1;
    }

    public void addAutostradaChoices() {
        this.autostradaChoices += 1;
    }

    public void addDirtRoadChoices() {
        this.dirtRoadChoices += 1;
    }

    public void subSpecialQuestionsRemaining() {
        this.specialQuestionsRemaining -= 1;
    }

    public void snapshotJunctionState() {
        this.lastJunctionRegularQCount = this.regularQAttempts;
    }

    public void addHintsReceived(){
        this.numHintsReceived += 1;
    }

    public long getQuestionTimeSpent() {
        long timeElapsed = System.currentTimeMillis() - this.questionStartTimeAtMs;

        return Math.min(timeElapsed, trackState.getTimeLimitMillis());
    }

    public static String assignRandomColor() {
        int randomIndex = ThreadLocalRandom.current().nextInt(CAR_COLORS.length);
        return CAR_COLORS[randomIndex];
    }

    @Override
    public String toString() {
        return "RacePlayer{" +
                "currentScore= " + currentScore +
                " id= " + getId()+
                " nickname= " + getNickname() +

                '}';
    }
}
