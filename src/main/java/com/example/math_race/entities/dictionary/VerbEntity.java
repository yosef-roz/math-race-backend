package com.example.math_race.entities.dictionary;

import com.example.math_race.entities.BaseEntity;
import com.example.math_race.json.models.dictionary.VerbJsonModel;
import com.example.math_race.questionGenerator.tags.enums.Gender;
import com.example.math_race.questionGenerator.tags.enums.Plurality;
import com.example.math_race.questionGenerator.tags.enums.Tense;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class VerbEntity extends BaseEntity {

    private String verbId;
    private Set<VerbFormEntry> dbForms = new HashSet<>();

    public VerbEntity(VerbJsonModel model) {
        this.verbId = model.id();

        for (Map.Entry<String, String> entry : model.forms().entrySet()) {
            String[] parts = entry.getKey().split("_");
            Tense tense = Tense.valueOf(parts[0]);
            Gender gender = Gender.valueOf(parts[1]);
            Plurality plurality = Plurality.valueOf(parts[2]);
            this.dbForms.add(new VerbFormEntry(tense,gender, plurality, entry.getValue()));
        }
    }

    public Map<String, String> getFormsAsMap() {
        Map<String, String> map = new HashMap<>();

        for (VerbFormEntry entry : dbForms) {
            String key = entry.getTense().name() + "_" +
                    entry.getGender().name() + "_" +
                    entry.getPlurality().name();
            map.put(key, entry.getValue());
        }
        return map;
    }
}
