package com.example.math_race.entities.dictionary;

import com.example.math_race.questionGenerator.tags.enums.Gender;
import com.example.math_race.questionGenerator.tags.enums.Plurality;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdjectiveFormEntry {
    private Gender gender;
    private Plurality plurality;
    private String value;
}
