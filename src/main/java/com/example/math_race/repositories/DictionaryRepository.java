package com.example.math_race.repositories;

import com.example.math_race.entities.BaseEntity;
import com.example.math_race.entities.dictionary.*;
import com.example.math_race.models.dictionary.DictionaryJsonSeeder;
import com.example.math_race.questionGenerator.tags.types.*;
import com.example.math_race.race.questions.MathQuestionGenerator;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Repository
public class DictionaryRepository extends BaseRepository {

    private final DictionaryJsonSeeder seeder;

    @Autowired
    public DictionaryRepository(SessionFactory sf, DictionaryJsonSeeder seeder) {
        super(sf);
        this.seeder = seeder;
    }

    public List<HumanTag> loadHumanTags() {
        return loadTagsOrSeed(
                HumanEntity.class,
                seeder::getHumanEntitiesFromJson,
                HumanTag::new
        );
    }

    public List<ItemTag> loadItemTag() {
        return loadTagsOrSeed(
                ItemEntity.class,
                seeder::getItemEntitiesFromJson,
                ItemTag::new
        );
    }

    public List<AdjectiveTag> loadAdjectiveTag() {
        return loadTagsOrSeed(
                AdjectiveEntity.class,
                seeder::getAdjectiveEntitiesFromJson,
                AdjectiveTag::new
        );
    }

    public List<VerbTag> loadVerbTag() {
        return MathQuestionGenerator.fillVerbs();
    }

    public List<PlaceTag> loadPlaceTag() {
        return loadTagsOrSeed(
                PlaceEntity.class,
                seeder::getPlaceEntitiesFromJson,
                PlaceTag::new
        );
    }

    public List<UnitTag> loadUnitTag() {
        return loadTagsOrSeed(
                UnitEntity.class,
                seeder::getUnitEntitiesFromJson,
                UnitTag::new
        );
    }

    public List<RoleTag> loadRoleTag() {
        return loadTagsOrSeed(
                RoleEntity.class,
                seeder::getRoleEntitiesFromJson,
                RoleTag::new
        );
    }

    private <E extends BaseEntity, T> List<T> loadTagsOrSeed(Class<E> entityClass, Supplier<List<E>> seedSupplier, Function<E, T> tagMapper) {
        List<E> entities = loadList(entityClass);

        if (entities.isEmpty()) {
            entities = seedSupplier.get();
            saveAll(entities);
        }

        return entities.stream()
                .map(tagMapper)
                .collect(Collectors.toList());
    }
}
