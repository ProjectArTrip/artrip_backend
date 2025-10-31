package org.atdev.artrip.domain.favortie.repository;

import org.springframework.data.repository.query.Param;
import org.atdev.artrip.domain.favortie.data.FavoriteExhibit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FavoriteExhibitRepository extends JpaRepository<FavoriteExhibit, Long> {
    boolean existsByUser_UserIdAndExhibit_ExhibitId(Long userId, Long exhibitId);

    @Query("SELECT f FROM FavoriteExhibit f " +
            "INNER JOIN FETCH f.exhibit e " +
            "INNER JOIN FETCH e.exhibitHall " +
            "WHERE f.user.userId = :userId " +
            "ORDER BY f.createdAt DESC ")
    List<FavoriteExhibit> findAllByUserIdWithExhibit(@Param("userId") Long userId);

    @Query("SELECT f FROM FavoriteExhibit f " +
            "INNER JOIN FETCH f.exhibit e " +
            "INNER JOIN fetch e.exhibitHall eh " +
            "WHERE f.user.userId = :userId " +
            "AND DATE(e.startDate) <= :date " +
            "AND DATE(e.endDate) >= :date " +
            "ORDER BY e.startDate ASC")
    List<FavoriteExhibit> findByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT f FROM FavoriteExhibit f " +
            "INNER JOIN f.exhibit e " +
            "INNER JOIN e.exhibitHall eh " +
            "WHERE f.user.userId = :userId " +
            "AND eh.country = :country " +
            "ORDER BY e.startDate DESC")
    List<FavoriteExhibit> findByUserIdAndCountry(@Param("userId") Long userId, @Param("country") String country);

    @Query(value = "SELECT DISTINCT DATE(e.start_date) " +
            "FROM favorite_exhibit f " +
            "INNER JOIN exhibit e ON f.exhibit_id = e.exhibit_id " +
            "WHERE f.user_id = :userId " +
            "AND YEAR (e.start_date) = :year " +
            "AND MONTH (e.start_date) = :month " +
            "ORDER BY DATE(e.start_date) ASC", nativeQuery = true)
    List<String> findExhibitDatesByUserIdAndYearMonth(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month);

    @Query("SELECT DISTINCT eh.country FROM FavoriteExhibit f " +
            "INNER JOIN f.exhibit e " +
            "INNER JOIN e.exhibitHall eh " +
            "WHERE f.user.userId = :userId " +
            "AND eh.country IS NOT NULL " +
            "ORDER BY eh.country ASC")
    List<String> findDistinctCountriesByUserId(@Param("userId") Long userId);

    @Query("SELECT f FROM FavoriteExhibit f " +
            "WHERE f.user.userId = :userId " +
            "AND f.exhibit.exhibitId = :exhibitId")
    Optional<FavoriteExhibit> findByUserIdAndExhibitId(@Param("userId") Long userId, @Param("exhibitId") Long exhibitId);
}
