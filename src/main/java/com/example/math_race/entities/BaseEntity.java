package com.example.math_race.entities;

import lombok.Data;

import java.util.Date;

@Data
public abstract class BaseEntity {
    private int id;
    private Date creationDate;
    private Date updatedDate;
    private boolean deleted;
    private Date deletionDate;

    public BaseEntity() {
        creationDate = new Date();
        deleted = false;
    }
}