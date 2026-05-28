package com.example.math_race.entities.dictionary;

import com.example.math_race.entities.BaseEntity;
import com.example.math_race.json.models.dictionary.ItemJsonModel;
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
public class ItemEntity extends BaseEntity {

    private String itemId;
    private String singular;
    private String plural;
    private Gender gender;
    private Set<ItemCategory> categories;
    private Set<UnitType> allowedUnits;

    public ItemEntity(ItemJsonModel model) {
        this.itemId = model.id();
        this.singular = model.singular();
        this.plural = model.plural();
        this.gender = model.gender();
        this.categories = model.categories();
        this.allowedUnits = model.allowedUnits();
    }
}
