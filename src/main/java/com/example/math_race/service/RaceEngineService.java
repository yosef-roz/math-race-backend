package com.example.math_race.service;

import com.example.math_race.dto.wsMessage.response.AnswerScoreDTO;
import com.example.math_race.dto.wsMessage.response.MathQuestionDTO;
import com.example.math_race.dto.wsMessage.response.RaceResultsDTO;
import com.example.math_race.dto.wsMessage.response.StatusChangedDTO;
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
        if (race == null || !race.getStatus().equals(RaceStatus.PENDING)) return;

        race.setStatus(RaceStatus.IN_PROGRESS);
        race.setLastResumedAtMillis(System.currentTimeMillis());

        Instant endTime = Instant.now().plusMillis(race.getRemainingTimeMillis());
        ScheduledFuture<?> endTask = scheduler.schedule(() -> finishRace(race), Date.from(endTime));

        raceEndTimers.put(race.getId(), endTask);

        StatusChangedDTO statusChangedDTO = new StatusChangedDTO(RaceStatus.IN_PROGRESS);
        webSocketService.sendSuccessToQueueSession(QUEUE_RACE_HOST, "RACE_START", statusChangedDTO,
                race.getHost().getId(), race.getHost().getSessionActive());
        webSocketService.sendSuccessToTopic(webSocketService.getRaceUpdatesTopic(race.getRoomCode()),
                "RACE_START", statusChangedDTO);


        for (RacePlayer player : race.getPlayers().values()) {
           sendNextQuestionToPlayer(race,player);
        }
    }

    public void sendNextQuestionToPlayer(RaceManager race,RacePlayer player) {
        if (!race.isAccountIn(player.getId()) || !race.getStatus().isRunning()) return;

        synchronized (player) {

            if (player.getCurrentQuestion() != null) {
                return;
            }

            ScheduledFuture<?> existingTimer = playerQuestionTimers.remove(player.getId());
            if (existingTimer != null) {
                existingTimer.cancel(false);
            }

            MathQuestion question = mathQuestionGenerator.generateForPlayer(player);
            player.setCurrentQuestion(question);

            player.setQuestionStartTimeMillis(System.currentTimeMillis());
            player.setQuestionRemainingTimeMillis(question.getTimeLimitMillis());

            MathQuestionDTO questionDTO = new MathQuestionDTO(race, player, question);
            webSocketService.sendSuccessToQueueSession(QUEUE_RACE_FEEDBACK, "NEW_QUESTION", questionDTO,
                    player.getId(), player.getSessionActive());

            webSocketService.sendSuccessToQueueSession(QUEUE_RACE_HOST, "QUESTION_SENT", questionDTO,
                    race.getHost().getId(), race.getHost().getSessionActive());


            Instant timeoutTime = Instant.now().plusMillis(question.getTimeLimitMillis());
            ScheduledFuture<?> timeoutTask = scheduler.schedule(() -> handleQuestionTimeout(race, player, question), Date.from(timeoutTime));


            playerQuestionTimers.put(player.getId(), timeoutTask);
        }
    }

    public void processPlayerAnswer(RaceManager race, RacePlayer player, String answer) {
        if (!race.getStatus().isRunning()) return;

        synchronized (player) {
            if (player.getCurrentQuestion() == null) {
                return;
            }

            ScheduledFuture<?> timer = playerQuestionTimers.remove(player.getId());
            if (timer != null) {
                timer.cancel(false);
            }

            boolean isCorrect = player.checkAnswer(answer);
            int addScore;
            if (isCorrect) {
                addScore = player.getCurrentQuestion().getScore();
                player.addScore(addScore);
            } else {
                addScore = -(int) (player.getCurrentQuestion().getScore() * 0.2);
                if (player.getCurrentScore() + addScore < 0)
                    addScore = 0;
                player.addScore(addScore);

            }

            AnswerScoreDTO answerScoreDTO = new AnswerScoreDTO(addScore, player.getId());

            webSocketService.sendSuccessToQueueSession(QUEUE_RACE_FEEDBACK, isCorrect ? "CORRECT_ANSWER" : "WRONG_ANSWER", answerScoreDTO,
                    player.getId(), player.getSessionActive());
            webSocketService.sendSuccessToQueueSession(QUEUE_RACE_HOST, isCorrect ? "PLAYER_ANSWERED_CORRECTLY" : "PLAYER_ANSWERED_INCORRECTLY", answerScoreDTO,
                    race.getHost().getId(), race.getHost().getSessionActive());

            player.setCurrentQuestion(null);


            if (player.getCurrentScore() >= race.getSettings().getTargetScore()) {
                finishRace(race);
            } else {
                scheduler.schedule(() -> {
                    sendNextQuestionToPlayer(race, player);
                }, Date.from(Instant.now().plusMillis(200)));

            }

        }
    }

    private void handleQuestionTimeout(RaceManager race, RacePlayer player, MathQuestion timedOutQuestion) {
        if (!race.getStatus().isRunning()) return;

        synchronized (player) {
            if (player.getCurrentQuestion() != timedOutQuestion) {
                return;
            }

            playerQuestionTimers.remove(player.getId());
            player.setCurrentQuestion(null);

            AnswerScoreDTO answerScoreDTO = new AnswerScoreDTO(0, player.getId());

            webSocketService.sendSuccessToQueueSession(QUEUE_RACE_FEEDBACK, "TIMEOUT", answerScoreDTO,
                    player.getId(), player.getSessionActive());
            webSocketService.sendSuccessToQueueSession(QUEUE_RACE_HOST, "PLAYER_TIMEOUT", answerScoreDTO,
                    race.getHost().getId(), race.getHost().getSessionActive());

            scheduler.schedule(() -> {
                sendNextQuestionToPlayer(race, player);
            }, Date.from(Instant.now().plusMillis(200)));
        }
    }

    private void resumeCurrentQuestionForPlayer(RaceManager race, RacePlayer player) {
        if (!race.isAccountIn(player.getId()) || !race.getStatus().isRunning()) return;

        if (player.getCurrentQuestion() == null) {
            sendNextQuestionToPlayer(race, player);
            return;
        }

        long remainingTime = player.getQuestionRemainingTimeMillis();
        player.setQuestionStartTimeMillis(System.currentTimeMillis());


        MathQuestion currentQuestion = player.getCurrentQuestion();
        Instant timeoutTime = Instant.now().plusMillis(remainingTime);
        ScheduledFuture<?> timeoutTask = scheduler.schedule(() -> handleQuestionTimeout(race, player,currentQuestion), Date.from(timeoutTime));

        playerQuestionTimers.put(player.getId(), timeoutTask);
    }

    public void pauseRace(RaceManager race) {
        if (race == null || !race.getStatus().isRunning()) return;

        long currentRemainingTime = race.getCalculatedRemainingTime();

        for (RacePlayer player : race.getPlayers().values()) {
            ScheduledFuture<?> timer = playerQuestionTimers.remove(player.getId());
            if (timer != null) {
                timer.cancel(false);

                long remainingTime = player.getCalculatedQuestionRemainingTime(race.getStatus());
                player.setQuestionRemainingTimeMillis(remainingTime);
            }
        }

        race.setStatus(RaceStatus.PAUSED);
        race.setRemainingTimeMillis(currentRemainingTime);
        race.setLastResumedAtMillis(0);

        ScheduledFuture<?> endTask = raceEndTimers.remove(race.getId());
        if (endTask != null) {
            endTask.cancel(false);
        }

        StatusChangedDTO statusChangedDTO = new StatusChangedDTO(RaceStatus.PAUSED);

        webSocketService.sendSuccessToTopic(webSocketService.getRaceUpdatesTopic(race.getRoomCode()), "RACE_PAUSED", statusChangedDTO);
        webSocketService.sendSuccessToQueueSession(QUEUE_RACE_HOST, "RACE_PAUSED", statusChangedDTO,
                race.getHost().getId(), race.getHost().getSessionActive());
    }

    public void resumeRace(RaceManager race) {
        if (race == null || !race.getStatus().equals(RaceStatus.PAUSED)) return;

        race.setStatus(RaceStatus.IN_PROGRESS);
        race.setLastResumedAtMillis(System.currentTimeMillis());

        Instant endTime = Instant.now().plusMillis(race.getRemainingTimeMillis());
        ScheduledFuture<?> endTask = scheduler.schedule(() -> finishRace(race), Date.from(endTime));
        raceEndTimers.put(race.getId(), endTask);

        StatusChangedDTO  statusChangedDTO = new StatusChangedDTO(RaceStatus.IN_PROGRESS);

        webSocketService.sendSuccessToTopic(webSocketService.getRaceUpdatesTopic(race.getRoomCode()), "RACE_RESUMED", statusChangedDTO);
        webSocketService.sendSuccessToQueueSession(QUEUE_RACE_HOST, "RACE_RESUMED", statusChangedDTO,
                race.getHost().getId(), race.getHost().getSessionActive());

        for (RacePlayer player : race.getPlayers().values()) {
            resumeCurrentQuestionForPlayer(race, player);
        }
    }

    private void finishRace(RaceManager race) {
        if (!race.getStatus().isRunning()) return;
        // צריך לדאוג לשמור ל DB וגם לנתק אותם מ WS

        race.setStatus(RaceStatus.FINISHED);
        race.setEndedAtMillis(System.currentTimeMillis());

        raceEndTimers.remove(race.getId());
        clearAllPlayerTimersForRoom(race);

        RaceResultsDTO resultsDTO = new RaceResultsDTO(race);

        webSocketService.sendSuccessToTopic(webSocketService.getRaceUpdatesTopic(race.getRoomCode()),"RACE_COMPLETED",resultsDTO);
        webSocketService.sendSuccessToQueueSession(QUEUE_RACE_HOST,"RACE_COMPLETED",resultsDTO,
                race.getHost().getId(),race.getHost().getSessionActive());

    }

    public void cancelledRace(RaceManager race) {
        if (race.getStatus().isClosed()) return;

        // צריך לדאוג לשמור ל DB וגם לנתק אותם מ WS

        race.setStatus(RaceStatus.CANCELLED);
        race.setEndedAtMillis(System.currentTimeMillis());

        ScheduledFuture<?> endTask = raceEndTimers.remove(race.getId());
        if (endTask != null) {
            endTask.cancel(false);
        }
        clearAllPlayerTimersForRoom(race);

        StatusChangedDTO  statusChangedDTO = new StatusChangedDTO(RaceStatus.CANCELLED);

        webSocketService.sendSuccessToTopic(webSocketService.getRaceUpdatesTopic(race.getRoomCode()),"RACE_CANCELLED",statusChangedDTO);
        webSocketService.sendSuccessToQueueSession(QUEUE_RACE_HOST,"RACE_CANCELLED",statusChangedDTO,
                race.getHost().getId(),race.getHost().getSessionActive());

    }

    private void clearAllPlayerTimersForRoom(RaceManager race) {
        for (RacePlayer player : race.getPlayers().values()) {
            ScheduledFuture<?> timer = playerQuestionTimers.remove(player.getId());
            if (timer != null) {
                timer.cancel(false);
            }
        }
    }
}