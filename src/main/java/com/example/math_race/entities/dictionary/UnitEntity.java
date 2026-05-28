package com.example.math_race.entities.dictionary;

import com.example.math_race.entities.BaseEntity;
import com.example.math_race.json.models.dictionary.UnitJsonModel;
import com.example.math_race.questionGenerator.tags.enums.Gender;
import com.example.math_race.questionGenerator.tags.enums.ItemCategory;
import com.example.math_race.questionGenerator.tags.enums.UnitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class UnitEntity extends BaseEntity {

    private String unitId;
    private String singular;
    private String plural;
    private Gender gender;
    private UnitType type;
    private Set<ItemCategory> validItemCategories;

    public UnitEntity(UnitJsonModel model) {
        this.unitId = model.id();
        this.singular = model.singular();
        this.plural = model.plural();
        this.gender = model.gender();
        this.type = model.type();
        this.validItemCategories = model.validItemCategories();
    }
}
