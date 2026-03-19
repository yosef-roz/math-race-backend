package com.example.math_race.race;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class RaceHost extends RaceAccount {

    public RaceHost(String accountId,String sessionActive, String joinToken,String nickname){
        super(accountId,sessionActive,joinToken,nickname);
    }

}
