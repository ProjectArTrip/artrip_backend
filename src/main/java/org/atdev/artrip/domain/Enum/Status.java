package org.atdev.artrip.domain.Enum;

public enum Status {
    UPCOMING,    // 예정된 전시
    ONGOING,     // 진행 중인 전시
    ENDING_SOON, // 마감 임박(3일전부터)
    FINISHED     // 종료된 전시
}