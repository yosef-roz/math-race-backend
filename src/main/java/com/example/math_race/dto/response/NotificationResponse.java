package com.example.math_race.dto.response;

import com.example.math_race.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationResponse<T> {
    private String type;      // "ERROR", "GAME_UPDATE", "CHAT"
    private String content;   // המזהה (למשל RACE_NOT_FOUND)
    private Integer code;     // הקוד המספרי (1404)
    private String message;   // ההודעה למשתמש (The race room...)
    private T data;           // מידע נוסף (הניקוד, רשימת שחקנים וכו')

    // Factory Method ליצירת שגיאה מה-Enum שלך
    public static NotificationResponse<Void> createError(ErrorCode error) {
        return NotificationResponse.<Void>builder()
                .type("ERROR")
                .content(error.name())
                .code(error.getCode())
                .message(error.getMessage())
                .build();
    }

    // הודעה כללית (למשל הודעת מערכת)
    public static <T> NotificationResponse<T> generalMessage(String message) {
        return NotificationResponse.<T>builder()
                .type("GENERAL_MESSAGE")
                .message(message)
                .build();
    }

    // הודעת הצלחה עם מידע (Payload)
    public static <T> NotificationResponse<T> success(String type, T data) {
        return NotificationResponse.<T>builder()
                .type(type)
                .data(data)
                .build();
    }
}