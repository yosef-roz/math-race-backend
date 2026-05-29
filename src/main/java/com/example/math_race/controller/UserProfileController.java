package com.example.math_race.controller;

import com.example.math_race.dto.http.ApiResponse;
import com.example.math_race.dto.http.request.RequestMetadata;
import com.example.math_race.dto.http.request.UpdateUsernameRequest;
import com.example.math_race.dto.http.response.*;
import com.example.math_race.exception.ErrorCode;
import com.example.math_race.exception.LogicException;
import com.example.math_race.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    @PatchMapping("/me/username")
    public ApiResponse<Void> updateUsername(@Valid @RequestBody UpdateUsernameRequest request, RequestMetadata metadata) {
        userProfileService.updateUsername(request,metadata);
        return ApiResponse.success(null);
    }


    @PostMapping("/me/delete-request")
    public ApiResponse<Void> requestAccountDeletion(RequestMetadata metadata) {
        userProfileService.requestAccountDeletion(metadata);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/me")
    public ApiResponse<Void> confirmAccountDeletion(@RequestParam("token") String deletionToken, RequestMetadata metadata) {
        if (!StringUtils.hasText(deletionToken)) {
            throw new LogicException(ErrorCode.INVALID_INPUT);
        }

        userProfileService.confirmAccountDeletion(deletionToken, metadata);
        return ApiResponse.success(null);
    }
}
