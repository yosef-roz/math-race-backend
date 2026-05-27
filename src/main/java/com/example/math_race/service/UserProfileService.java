package com.example.math_race.service;

import com.example.math_race.dto.http.request.RequestMetadata;
import com.example.math_race.dto.http.response.ProfileResponse;
import com.example.math_race.dto.http.response.RaceHistoryDetailsResponse;
import com.example.math_race.dto.http.response.RaceHistorySummaryResponse;
import com.example.math_race.dto.http.response.UserOverallStatisticsResponse;
import com.example.math_race.entities.UserEntity;
import com.example.math_race.repositories.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserProfileService {

    private final UserProfileRepository  userProfileRepository;
    private final AuthService authService;

    @Autowired
    public UserProfileService(UserProfileRepository userProfileRepository,  AuthService authService) {
        this.userProfileRepository = userProfileRepository;
        this.authService = authService;
    }

    public ProfileResponse getProfile(RequestMetadata metadata) {
       UserEntity user = authService.getValidUser(metadata);
        return new ProfileResponse(user.getUsername(), user.getEmail());
    }


    public UserOverallStatisticsResponse getStatistics (RequestMetadata metadata) {
        UserEntity user = authService.getValidUser(metadata);
        return userProfileRepository.getUserOverallStatistics(user);
    }

    public List<RaceHistorySummaryResponse> getHistorySummaries(RequestMetadata metadata) {
        UserEntity user = authService.getValidUser(metadata);
        return userProfileRepository.getRaceHistorySummary(user);
    }

    public RaceHistoryDetailsResponse getSpecificRaceDetails(String raceId, RequestMetadata metadata) {
        UserEntity user = authService.getValidUser(metadata);
        return userProfileRepository.getRaceHistoryDetails(user, raceId);
    }

}
