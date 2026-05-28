package com.example.math_race.entities.dictionary;

import com.example.math_race.entities.BaseEntity;
import com.example.math_race.json.models.dictionary.RoleJsonModel;
import com.example.math_race.questionGenerator.tags.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RoleEntity extends BaseEntity {

    private String roleId;
    private String singularMale;
    private String pluralMale;
    private String singularFemale;
    private String pluralFemale;
    private RoleType roleType;
    private Set<String> validPlaceIds;

    public RoleEntity(RoleJsonModel model){
        this.roleId = model.id();
        this.singularMale = model.singularMale();
        this.pluralMale = model.pluralMale();
        this.singularFemale = model.singularFemale();
        this.pluralFemale = model.pluralFemale();
        this.roleType = model.roleType();
        this.validPlaceIds = model.validPlaceIds();
    }
}
