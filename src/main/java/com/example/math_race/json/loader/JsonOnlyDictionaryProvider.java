package com.example.math_race.json.loader;

import com.example.math_race.json.models.seeders.DictionaryJsonSeeder;
import com.example.math_race.questionGenerator.dictionary.DictionaryProvider;
import com.example.math_race.questionGenerator.tags.types.*;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class JsonOnlyDictionaryProvider implements DictionaryProvider {

    private final DictionaryJsonSeeder seeder;

    public JsonOnlyDictionaryProvider(DictionaryJsonSeeder seeder) {
        this.seeder = seeder;
    }

    @Override
    public List<HumanTag> loadHumanTags() {
        return loadTags(seeder::getHumanEntitiesFromJson, HumanTag::new);
    }

    @Override
    public List<ItemTag> loadItemTag() {
        return loadTags(seeder::getItemEntitiesFromJson, ItemTag::new);
    }

    @Override
    public List<VerbTag> loadVerbTag() {
        return loadTags(seeder::getVerbEntitiesFromJson, VerbTag::new);
    }

    @Override
    public List<PlaceTag> loadPlaceTag() {
        return loadTags(seeder::getPlaceEntitiesFromJson, PlaceTag::new);
    }

    @Override
    public List<AdjectiveTag> loadAdjectiveTag() {
        return loadTags(seeder::getAdjectiveEntitiesFromJson, AdjectiveTag::new);
    }

    @Override
    public List<UnitTag> loadUnitTag() {
        return loadTags(seeder::getUnitEntitiesFromJson, UnitTag::new);
    }

    @Override
    public List<RoleTag> loadRoleTag() {
        return loadTags(seeder::getRoleEntitiesFromJson, RoleTag::new);
    }

    private <E, T> List<T> loadTags(Supplier<List<E>> seedSupplier, Function<E, T> tagMapper) {
        return seedSupplier.get().stream()
                .map(tagMapper)
                .collect(Collectors.toList());
    }
}
