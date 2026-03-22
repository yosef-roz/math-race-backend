package com.example.math_race.race.questions;

public class Verb {
    String pastM;
    String pastF;
    String presentM;
    String presentF;
    String futureM;
    String futureF;

    public String get(Gender gender, Tense tense) {
        return switch (tense) {
            case PAST -> (gender == Gender.MALE) ? pastM : pastF;
            case PRESENT -> (gender == Gender.MALE) ? presentM : presentF;
            case FUTURE -> (gender == Gender.MALE) ? futureM : futureF;
        };
    }
}
