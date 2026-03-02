package com.example.math_race.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity {
    private int id;
    private Date creationDate = new Date();
    private Date updatedDate;
    private boolean deleted = false;
    private Date deletionDate;
}