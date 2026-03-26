package com.example.math_race.questionGenerator.tags.types;

import com.example.math_race.questionGenerator.tags.core.QuestionEntity;

public class TimeTag implements QuestionEntity {
    private int totalMinutes;

    public TimeTag(int totalMinutes) {
        this.totalMinutes = totalMinutes;
    }

    @Override
    public String getProperty(String key) {
        int resultMinutes = totalMinutes;

        // הוספת דקות
        if (key.startsWith("add_m_")) {
            int add = Integer.parseInt(key.replace("add_m_", ""));
            resultMinutes = (totalMinutes + add) % 1440;
        }
        // חיסור דקות
        else if (key.startsWith("sub_m_")) {
            int sub = Integer.parseInt(key.replace("sub_m_", ""));
            resultMinutes = (totalMinutes - sub) % 1440;
            if (resultMinutes < 0) resultMinutes += 1440;
        }
        // הוספת שעות
        else if (key.startsWith("add_h_")) {
            int add = Integer.parseInt(key.replace("add_h_", ""));
            resultMinutes = (totalMinutes + add * 60) % 1440;
        }
        // חיסור שעות
        else if (key.startsWith("sub_h_")) {
            int sub = Integer.parseInt(key.replace("sub_h_", ""));
            resultMinutes = (totalMinutes - sub * 60) % 1440;
            if (resultMinutes < 0) resultMinutes += 1440;
        }

        // חישוב חזרה לשעות ודקות
        int h = resultMinutes / 60;
        int m = resultMinutes % 60;

        // החזרת מחרוזת בפורמט HH:mm (למשל 08:05 או 14:30)
        return String.format("%02d:%02d", h, m);
    }

    public int getTotalMinutes() {
        return totalMinutes;
    }
}