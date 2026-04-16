package com.example.math_race.repositories;

import com.example.math_race.questionGenerator.tags.types.*;
import com.example.math_race.race.questions.MathQuestionGenerator;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DictionaryRepository extends BaseRepository {

    @Autowired
    public DictionaryRepository(SessionFactory sf) {
        super(sf);
    }

    public List<HumanTag> loadHumanTags() {
        return MathQuestionGenerator.fillHumans();
    }

    public List<ItemTag> loadItemTag() {
        return MathQuestionGenerator.fillItems();
    }

    public List<VerbTag> loadVerbTag() {
        return MathQuestionGenerator.fillVerbs();
    }

    public List<PlaceTag> loadPlaceTag() {
        return MathQuestionGenerator.fillPlaces();
    }

    public List<AdjectiveTag> loadAdjectiveTag() {
        return MathQuestionGenerator.fillAdjectives();
    }

    public List<UnitTag> loadUnitTag() {
        return MathQuestionGenerator.fillUnits();
    }

    public List<RoleTag> loadRoleTag() {
        return MathQuestionGenerator.fillRoles();
    }

    public List<VehicleTag> loadVehicleTag() {
        return MathQuestionGenerator.fillVehicles();
    }
}
