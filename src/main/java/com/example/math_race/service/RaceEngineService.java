package com.example.math_race.service;

import com.example.math_race.dto.wsMessage.ChangeRaceStatusDTO;
import com.example.math_race.dto.wsMessage.NewQuestionDTO;
import com.example.math_race.race.RaceManager;
import com.example.math_race.race.RacePlayer;
import com.example.math_race.race.RaceStatus;
import com.example.math_race.race.questions.MathQuestion;
import com.example.math_race.race.questions.MathQuestionGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import static com.example.math_race.config.GameSchedulerConfig.GAME_SCHEDULER_BEAN_NAME;
import static com.example.math_race.service.WebSocketService.*;

@Service
public class RaceEngineService {

    private final ThreadPoolTaskScheduler scheduler;
    private final WebSocketService webSocketService;
    private final MathQuestionGenerator mathQuestionGenerator;
    private final Map<String, ScheduledFuture<?>> raceEndTimers;
    private final Map<String, ScheduledFuture<?>> playerQuestionTimers;

    @Autowired
    public RaceEngineService(
            @Qualifier(GAME_SCHEDULER_BEAN_NAME) ThreadPoolTaskScheduler scheduler, WebSocketService webSocketService,
            MathQuestionGenerator mathQuestionGenerator) {

        this.scheduler = scheduler;
        this.webSocketService = webSocketService;
        this.mathQuestionGenerator = mathQuestionGenerator;
        this.raceEndTimers = new ConcurrentHashMap<>();
        this.playerQuestionTimers = new ConcurrentHashMap<>();
    }

    public void startRace(RaceManager race) {
        if (race == null || !race.isPending()) return;

        race.setStatus(RaceStatus.IN_PROGRESS);
        race.setLastResumedAtMillis(System.currentTimeMillis());

        Instant endTime = Instant.now().plusMillis(race.getRemainingTimeMillis());
        ScheduledFuture<?> endTask = scheduler.schedule(() -> {
           // finishRace(race);
        }, Date.from(endTime));

        raceEndTimers.put(race.getId(), endTask);

        ChangeRaceStatusDTO statusDTO = new ChangeRaceStatusDTO(RaceStatus.IN_PROGRESS.name());

        webSocketService.sendSuccessToTopic(webSocketService.getRaceUpdatesTopic(race.getRoomCode()),
                "RACE_START", statusDTO);

        webSocketService.sendSuccessToQueueSession(QUEUE_RACE_HOST, "RACE_START", statusDTO,
                race.getHost().getId(), race.getHost().getSessionActive());


        for (RacePlayer player : race.getPlayers().values()) {
           sendNextQuestionToPlayer(race,player);
        }
    }

    public void sendNextQuestionToPlayer(RaceManager race,RacePlayer player) {
        if (!race.isAccountIn(player.getId()) || !race.isInProgress()) return;

        ScheduledFuture<?> oldTimer = playerQuestionTimers.remove(player.getId());
        if (oldTimer != null) {
            oldTimer.cancel(false);
        }

        MathQuestion question = mathQuestionGenerator.generateForPlayer(player);
        player.setCurrentQuestion(question);

        NewQuestionDTO questionDTO = new NewQuestionDTO(question, player);
        webSocketService.sendSuccessToQueueSession(
                QUEUE_RACE_FEEDBACK,
                "NEW_QUESTION",
                questionDTO,
                player.getId(),
                player.getSessionActive()
        );

        Instant timeoutTime = Instant.now().plusMillis(question.getTimeLimitMillis());
        ScheduledFuture<?> timeoutTask = scheduler.schedule(() -> {
           // handleQuestionTimeout(race, player);
        }, Date.from(timeoutTime));


        playerQuestionTimers.put(player.getId(), timeoutTask);
    }

    // 3. השחקן שלח תשובה
//    public void processPlayerAnswer(String roomCode, String playerId, String answer) {
//        // צעד ראשון: עוצרים את הטיימר של השאלה!
//        ScheduledFuture<?> timer = playerQuestionTimers.remove(playerId);
//        if (timer != null) {
//            timer.cancel(false); // false אומר - אם כבר התחיל לצפצף, אל תעצור באלימות
//        }
//
//        // בדיקת התשובה ועדכון הניקוד...
//        boolean isCorrect = checkAnswer(playerId, answer);
//        if (isCorrect) {
//            addScore(playerId);
//            broadcastProgress(roomCode); // עדכון כולם במסך ה-Host ובמסכי השחקנים
//        }
//
//        // שולחים את השאלה הבאה (שתייצר טיימר חדש ושאלה חדשה)
//        sendNextQuestionToPlayer(roomCode, playerId);
//    }

    // 4. נגמר הזמן לשאלה!
//    private void handleQuestionTimeout(RaceManager race, RacePlayer player) {
//        System.out.println("Player " + playerId + " ran out of time!");
//        playerQuestionTimers.remove(playerId); // מנקים מהמפה
//
//        // אפשר לשלוח לשחקן הודעה של "נגמר הזמן!"
//        messagingTemplate.convertAndSendToUser(playerId, "/queue/race/feedback", "TIMEOUT");
//
//        // מיד שולחים לו שאלה חדשה
//        sendNextQuestionToPlayer(roomCode, playerId);
//    }

    // 5. סיום המרוץ
//    private void finishRace(RaceManager race) {
//        System.out.println("Race " + roomCode + " has ended!");
//        raceEndTimers.remove(roomCode);
//
//        // כאן מעדכנים סטטוס ל-FINISHED ושולחים לכל השחקנים לעבור למסך התוצאות
//        messagingTemplate.convertAndSend("/topic/race/" + roomCode + "/end", getFinalResults(roomCode));
//
//        // בונוס: ניקוי כל הטיימרים של השחקנים ששייכים לחדר הזה כדי לא לבזבז זיכרון
//        clearAllPlayerTimersForRoom(roomCode);
//    }
}