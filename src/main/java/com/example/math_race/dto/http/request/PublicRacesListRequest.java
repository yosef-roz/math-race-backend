package com.example.math_race.dto.http.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicRacesListRequest {

    @Min(value = 0, message = "Page number must be 0 or greater")
    private int page;

    @Min(value = 1, message = "Size must be at least 1")
    private int size;
}
