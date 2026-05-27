package com.example.math_race.dto.http.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateGuestTokenResponse {
    private String guestToken;
    private int dayToSave;
}
