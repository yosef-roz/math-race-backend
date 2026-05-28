package com.example.math_race.entities.dictionary;

import com.example.math_race.entities.BaseEntity;
import com.example.math_race.json.models.dictionary.HumanJsonModel;
import com.example.math_race.questionGenerator.tags.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class HumanEntity extends BaseEntity {

    private String name;
    private Gender gender;

    public HumanEntity(HumanJsonModel model) {
        this.name = model.name();
        this.gender = model.gender();
    }
}
