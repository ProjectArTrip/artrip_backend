package org.atdev.artrip.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DashboardStatistics {

    private Long totalExhibits;
    private Long ongoingExhibits;
    private Long upcomingExhibits;
    private Long finishedExhibits;

    private Long totalUsers;
    private Long newUsersToday;
    private Long activeUsers;

    private Long totalReviews;
    private Long pendingReviews;
    private Long reportedReviews;

    private Long totalSearches;
    private Long searchesToday;

    private Long totalStamps;
    private Long stampsIssuedToday;

}
