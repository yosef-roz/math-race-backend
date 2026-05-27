package com.example.math_race.models.dictionary;

import com.example.math_race.entities.dictionary.AdjectiveEntity;
import com.example.math_race.entities.dictionary.HumanEntity;
import com.example.math_race.entities.dictionary.ItemEntity;
import com.example.math_race.entities.dictionary.PlaceEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DictionaryJsonSeeder {

    private static final String ADJECTIVES_JSON_PATH = "/dictionary_data/adjectives.json";
    private static final String HUMANS_JSON_PATH = "/dictionary_data/humans.json";
    private static final String ITEMS_JSON_PATH = "/dictionary_data/items.json";
    private static final String PLACE_JSON_PATH = "/dictionary_data/places.json";

    private final ObjectMapper mapper = new ObjectMapper();

    public List<HumanEntity> getHumanEntitiesFromJson() {
        return loadEntitiesFromJson(HUMANS_JSON_PATH, new TypeReference<List<HumanJsonModel>>() {}, HumanEntity::new);
    }

    public List<AdjectiveEntity> getAdjectiveEntitiesFromJson() {
        return loadEntitiesFromJson(ADJECTIVES_JSON_PATH, new TypeReference<List<AdjectiveJsonModel>>() {}, AdjectiveEntity::new);
    }

    public List<ItemEntity> getItemEntitiesFromJson() {
        return loadEntitiesFromJson(ITEMS_JSON_PATH, new TypeReference<List<ItemJsonModel>>() {}, ItemEntity::new);
    }

    public List<PlaceEntity> getPlaceEntitiesFromJson() {
        return loadEntitiesFromJson(PLACE_JSON_PATH, new TypeReference<List<PlaceJsonModel>>() {}, PlaceEntity::new);
    }

    private <M, E> List<E> loadEntitiesFromJson(String jsonPath, TypeReference<List<M>> typeReference, Function<M, E> mapperFunction) {
        try {
            InputStream inputStream = getClass().getResourceAsStream(jsonPath);
            if (inputStream == null) {
                throw new RuntimeException("Cannot find " + jsonPath + " in resources");
            }

            List<M> jsonModels = mapper.readValue(inputStream, typeReference);

            return jsonModels.stream()
                    .map(mapperFunction)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Failed to load entities from JSON at path: " + jsonPath, e);
        }
    }
}
