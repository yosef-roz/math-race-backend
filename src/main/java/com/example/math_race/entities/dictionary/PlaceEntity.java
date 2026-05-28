package com.example.math_race.entities.dictionary;

import com.example.math_race.entities.BaseEntity;
import com.example.math_race.json.models.dictionary.PlaceJsonModel;
import com.example.math_race.questionGenerator.tags.enums.Gender;
import com.example.math_race.questionGenerator.tags.enums.ItemCategory;
import com.example.math_race.questionGenerator.tags.enums.PlaceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PlaceEntity extends BaseEntity {

    private String placeId;
    private String singular;
    private String plural;
    private Gender gender;
    private PlaceType placeType;
    private Set<ItemCategory> availableItemCategories;

    public PlaceEntity(PlaceJsonModel model){
        this.placeId = model.id();
        this.singular = model.singular();
        this.plural = model.plural();
        this.gender = model.gender();
        this.placeType = model.placeType();
        this.availableItemCategories = model.availableItemCategories();
    }

}
