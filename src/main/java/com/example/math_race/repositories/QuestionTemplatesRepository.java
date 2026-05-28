package com.example.math_race.repositories;

import com.example.math_race.entities.templates.QuestionTemplateEntity;
import com.example.math_race.json.models.seeders.TemplateJsonSeeder;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class QuestionTemplatesRepository extends BaseRepository {

    private final TemplateJsonSeeder seeder;

    @Autowired
    public QuestionTemplatesRepository(SessionFactory sf, TemplateJsonSeeder seeder) {
        super(sf);
        this.seeder = seeder;
    }

    public List<QuestionTemplateEntity> loadAllTemplates() {
        List<QuestionTemplateEntity> entities = loadList(QuestionTemplateEntity.class);

        if (entities.isEmpty()) {
            entities = seeder.getAllTemplateEntitiesFromJson();
            saveAll(entities);
        }

        return entities;
    }
}
