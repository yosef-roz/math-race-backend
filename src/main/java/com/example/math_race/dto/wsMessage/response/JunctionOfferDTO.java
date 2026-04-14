package com.example.math_race.dto.wsMessage.response;

import com.example.math_race.race.PlayerTrackState;
import com.example.math_race.race.RaceManager;
import com.example.math_race.race.RacePlayer;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class JunctionOfferDTO {

    private String playerId;
    private long timeLimitMillis;
    private PlayerTrackState state;
    private long questionRemainingTimeMillis;
    private String expression;
    private String offer1;
    private String offer2;

    public JunctionOfferDTO(RaceManager race, RacePlayer player){
        this.offer1 = PlayerTrackState.AUTOSTRADA.name();
        this.offer2 = PlayerTrackState.DIRT_ROAD.name();
        this.state = player.getTrackState();
        this.playerId = player.getId();
        this.timeLimitMillis = player.getTrackState().getTimeLimitMillis();
        this.questionRemainingTimeMillis = player.getCalculatedQuestionRemainingTime(race.getStatus());
        this.expression ="ברוך הבא לצומת! הינך עומד בפני שני דרכים חדשות, עליך לבחור בחוכמה את דרכך." +
                " אוטוסטרדה - שאלה קשה בעל סיכון גבוה אך בעל תגמול זהה." +
                " שביל עפר - רצף שאלות קלות בעלי סיכון נמוך עם ניקוד בהתאם. " +
                "בחר את דרכך!.";
    }
}
