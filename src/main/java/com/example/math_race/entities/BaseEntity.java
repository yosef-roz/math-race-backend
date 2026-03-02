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
    private Date creationDate;
    private Date updatedDate;
    private boolean deleted;
    private Date deletionDate;
}