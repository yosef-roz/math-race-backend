package com.example.math_race.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateGuestIdResponse {
    private String guestId;
    private int dayToSave;
}
