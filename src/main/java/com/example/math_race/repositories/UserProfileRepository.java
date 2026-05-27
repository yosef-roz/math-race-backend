package com.example.math_race.repositories;

import com.example.math_race.dto.http.response.RaceHistoryDetailsResponse;
import com.example.math_race.dto.http.response.RaceHistorySummaryResponse;
import com.example.math_race.dto.http.response.UserOverallStatisticsResponse;
import com.example.math_race.entities.RaceHistoryEntity;
import com.example.math_race.entities.RaceParticipantHistoryEntity;
import com.example.math_race.entities.UserEntity;
import com.example.math_race.exception.ErrorCode;
import com.example.math_race.exception.LogicException;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
@Repository
public class UserProfileRepository extends BaseRepository {

    @Autowired
    public UserProfileRepository(SessionFactory sf) {
        super(sf);
    }

    public UserOverallStatisticsResponse getUserOverallStatistics(UserEntity user) {
        String hql = "SELECT " +
                "COUNT(p.id), " +
                "MAX(p.maxRegularStreak), " +
                "SUM(p.regularQSuccesses), " +
                "SUM(p.regularQAttempts), " +
                "SUM(p.regularSuccessTimeMs) " +
                "FROM RaceParticipantHistoryEntity p " +
                "WHERE p.user = :user AND p.deleted = false";

        Object[] stats = (Object[]) getCurrentSession()
                .createQuery(hql)
                .setParameter("user", user)
                .uniqueResult();

        if (stats == null || stats[0] == null || (Long) stats[0] == 0) {
            return new UserOverallStatisticsResponse(0, 0, 0, 0.0, 0.0);
        }

        long totalRaces = (Long) stats[0];
        int maxStreak = stats[1] != null ? (Integer) stats[1] : 0;
        long totalSuccesses = stats[2] != null ? (Long) stats[2] : 0;
        long totalAttempts = stats[3] != null ? (Long) stats[3] : 0;
        long totalSuccessTime = stats[4] != null ? (Long) stats[4] : 0;

        String victoryHql = "SELECT COUNT(p.id) " +
                "FROM RaceParticipantHistoryEntity p " +
                "JOIN p.race r " +
                "WHERE p.user = :user " +
                "AND p.finalScore >= r.targetScore " +
                "AND p.deleted = false";

        long victories = (Long) getCurrentSession()
                .createQuery(victoryHql)
                .setParameter("user", user)
                .uniqueResult();

        double avgAccuracy = totalAttempts > 0
                ? ((double) totalSuccesses / totalAttempts) * 100
                : 0.0;

        double avgSuccessTime = totalSuccesses > 0
                ? (double) totalSuccessTime / totalSuccesses
                : 0.0;

        return new UserOverallStatisticsResponse(
                (int) totalRaces,
                (int) victories,
                maxStreak,
                avgAccuracy,
                avgSuccessTime
        );
    }

    public List<RaceHistorySummaryResponse> getRaceHistorySummary(UserEntity user) {
        String hql = "SELECT r, p FROM RaceHistoryEntity r " +
                "LEFT JOIN RaceParticipantHistoryEntity p " +
                "  ON p.race = r AND p.user = :userEntity AND p.deleted = false " +
                "WHERE r.deleted = false " +
                "AND (r.hostId = :userIdString OR p.id IS NOT NULL) " +
                "ORDER BY r.createdAtMs DESC";

        Query<Object[]> query = getCurrentSession().createQuery(hql, Object[].class);

        query.setParameter("userIdString", user.getId().toString());
        query.setParameter("userEntity", user);

        List<Object[]> results = query.list();

        return results.stream()
                .map(row -> {
                    RaceHistoryEntity race = (RaceHistoryEntity) row[0];
                    RaceParticipantHistoryEntity participant = (RaceParticipantHistoryEntity) row[1];

                    boolean isHost = race.getHostId().equals(user.getId().toString());

                    Integer rank = null;
                    if (participant != null) {
                        rank = participant.getRank();
                    }

                    return new RaceHistorySummaryResponse(race, isHost, rank);
                })
                .collect(Collectors.toList());
    }



    public RaceHistoryDetailsResponse getRaceHistoryDetails(UserEntity user, String raceId) {
        UUID raceUuid;
        try {
            raceUuid = UUID.fromString(raceId);
        } catch (IllegalArgumentException e) {
            throw new LogicException(ErrorCode.RACE_HISTORY_NOT_FOUND);
        }

        RaceHistoryEntity race = loadObject(RaceHistoryEntity.class, raceUuid);
        if (race == null || race.isDeleted()) {
            throw new LogicException(ErrorCode.RACE_HISTORY_NOT_FOUND);
        }

        String hql = "SELECT p FROM RaceParticipantHistoryEntity p " +
                "LEFT JOIN FETCH p.user " +
                "WHERE p.race = :race " +
                "AND p.deleted = false " +
                "ORDER BY p.rank ASC";

        List<RaceParticipantHistoryEntity> participants = getCurrentSession()
                .createQuery(hql, RaceParticipantHistoryEntity.class)
                .setParameter("race", race)
                .list();

        boolean isHost = race.getHostId().equals(user.getId().toString());
        Integer userRank = null;

        for (RaceParticipantHistoryEntity p : participants) {
            if (p.getUser() != null && p.getUser().getId().equals(user.getId())) {
                userRank = p.getRank();
                break;
            }
        }

        if (userRank == null && !isHost) {
            throw new LogicException(ErrorCode.RACE_HISTORY_ACCESS_DENIED);
        }

        return new RaceHistoryDetailsResponse(race, participants, isHost, userRank);
    }
}
