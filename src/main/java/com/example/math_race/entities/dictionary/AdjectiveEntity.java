package com.example.math_race.entities.dictionary;

import com.example.math_race.entities.BaseEntity;
import com.example.math_race.models.dictionary.AdjectiveJsonModel;
import com.example.math_race.questionGenerator.tags.enums.AdjectiveType;
import com.example.math_race.questionGenerator.tags.enums.Gender;
import com.example.math_race.questionGenerator.tags.enums.Plurality;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class AdjectiveEntity extends BaseEntity {

    private String adjectiveId;
    private AdjectiveType type;
    private Set<AdjectiveFormEntry> dbForms = new HashSet<>();

    public AdjectiveEntity(AdjectiveJsonModel model) {
        this.adjectiveId = model.id();
        this.type = model.type();

        for (Map.Entry<String, String> entry : model.forms().entrySet()) {
            String[] parts = entry.getKey().split("_");
            Gender gender = Gender.valueOf(parts[0]);
            Plurality plurality = Plurality.valueOf(parts[1]);
            this.dbForms.add(new AdjectiveFormEntry(gender, plurality, entry.getValue()));
        }
    }


    public Map<String, String> getFormsAsMap() {
        Map<String, String> map = new HashMap<>();

        for (AdjectiveFormEntry entry : dbForms) {
            String key = entry.getGender().name() + "_" + entry.getPlurality();
            map.put(key, entry.getValue());
        }

        return map;
    }
}
