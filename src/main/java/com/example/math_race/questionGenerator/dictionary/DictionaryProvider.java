package com.example.math_race.questionGenerator.dictionary;

import com.example.math_race.questionGenerator.tags.types.*;
import java.util.List;

public interface DictionaryProvider {
    List<HumanTag> loadHumanTags();
    List<ItemTag> loadItemTag();
    List<VerbTag> loadVerbTag();
    List<PlaceTag> loadPlaceTag();
    List<AdjectiveTag> loadAdjectiveTag();
    List<UnitTag> loadUnitTag();
    List<RoleTag> loadRoleTag();
}
