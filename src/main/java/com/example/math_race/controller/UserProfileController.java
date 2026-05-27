package com.example.math_race.controller;

import com.example.math_race.dto.http.ApiResponse;
import com.example.math_race.dto.http.request.RequestMetadata;
import com.example.math_race.dto.http.response.*;

import com.example.math_race.exception.ErrorCode;
import com.example.math_race.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Autowired
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/me")
    public ApiResponse<ProfileResponse> getUserProfile(RequestMetadata metadata) {
         ProfileResponse profile = userProfileService.getProfile(metadata);
         return ApiResponse.success(profile);
    }

    @GetMapping("/me/statistics")
    public ApiResponse<UserOverallStatisticsResponse> getUserOverallStatistics(RequestMetadata metadata) {
        UserOverallStatisticsResponse userStatistics = userProfileService.getStatistics(metadata);
        return ApiResponse.success(userStatistics);
    }

    @GetMapping("/me/history")
    public ApiResponse<List<RaceHistorySummaryResponse>> getUserRaceHistory(RequestMetadata metadata) {
        List<RaceHistorySummaryResponse> historySummaries = userProfileService.getHistorySummaries(metadata);
        return ApiResponse.success(historySummaries);
    }

    @GetMapping("/me/history/{raceId}")
    public ApiResponse<RaceHistoryDetailsResponse> getSpecificRaceHistory(@PathVariable String raceId, RequestMetadata metadata) {
        if (!StringUtils.hasText(raceId)) {
            return ApiResponse.error(ErrorCode.INVALID_INPUT);
        }

        RaceHistoryDetailsResponse raceDetails = userProfileService.getSpecificRaceDetails(raceId, metadata);
        return ApiResponse.success(raceDetails);
    }
}
