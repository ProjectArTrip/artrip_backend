package org.atdev.artrip.domain.favortie.repository;

import org.atdev.artrip.domain.auth.data.User;
import org.springframework.data.repository.query.Param;
import org.atdev.artrip.domain.favortie.data.FavoriteExhibit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FavoriteExhibitRepository extends JpaRepository<FavoriteExhibit, Long> {
    @Query("""
    SELECT COUNT(f) > 0
        FROM FavoriteExhibit f
        WHERE f.user.userId = :userId
        AND f.exhibit.exhibitId = :exhibitId
        And f.status = true 
    """)
    boolean existsActive(@Param("userId") Long userId, @Param("exhibitId") Long exhibitId);

    @Query("SELECT f " +
            "FROM FavoriteExhibit f " +
            "INNER JOIN FETCH f.exhibit e " +
            "INNER JOIN FETCH e.exhibitHall " +
            "WHERE f.user.userId = :userId " +
            "AND f.status = true " +
            "ORDER BY f.createdAt DESC ")
    List<FavoriteExhibit> findAllActive(@Param("userId") Long userId);

    @Query("SELECT f " +
            "FROM FavoriteExhibit f " +
            "INNER JOIN FETCH f.exhibit e " +
            "INNER JOIN fetch e.exhibitHall eh " +
            "WHERE f.user.userId = :userId " +
            "AND f.status = true " +
            "AND DATE(e.startDate) <= :date " +
            "AND DATE(e.endDate) >= :date " +
            "ORDER BY e.startDate ASC")
    List<FavoriteExhibit> findActiveByDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT f " +
            "FROM FavoriteExhibit f " +
            "INNER JOIN f.exhibit e " +
            "INNER JOIN e.exhibitHall eh " +
            "WHERE f.user.userId = :userId " +
            "AND f.status = true " +
            "AND eh.country = :country " +
            "ORDER BY e.startDate DESC")
    List<FavoriteExhibit> findActiveByCountry(@Param("userId") Long userId, @Param("country") String country);

    @Query(value = "SELECT DISTINCT DATE(e.start_date) " +
            "FROM favorite_exhibit f " +
            "INNER JOIN exhibit e ON f.exhibit_id = e.exhibit_id " +
            "WHERE f.user_id = :userId " +
            "AND f.status = true " +
            "AND YEAR (e.start_date) = :year " +
            "AND MONTH (e.start_date) = :month " +
            "ORDER BY DATE(e.start_date) ASC", nativeQuery = true)
    List<String> findDatesByYearMonth(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month);

    @Query("SELECT DISTINCT eh.country " +
            "FROM FavoriteExhibit f " +
            "INNER JOIN f.exhibit e " +
            "INNER JOIN e.exhibitHall eh " +
            "WHERE f.user.userId = :userId " +
            "AND f.status = true " +
            "AND eh.country IS NOT NULL " +
            "ORDER BY eh.country ASC")
    List<String> findDistinctCountries(@Param("userId") Long userId);

    @Query("SELECT f " +
            "FROM FavoriteExhibit f " +
            "WHERE f.user.userId = :userId " +
            "AND f.exhibit.exhibitId = :exhibitId")
    Optional<FavoriteExhibit> findByUserIdAndExhibitId(@Param("userId") Long userId, @Param("exhibitId") Long exhibitId);

    @Query("""
    SELECT eh.country as country, COUNT(f) as count 
    FROM FavoriteExhibit f
    INNER JOIN f.exhibit e
    INNER JOIN e.exhibitHall eh
    WHERE f.user.userId = :userId
    AND eh.country IS NOT NULL 
    GROUP BY eh.country
    ORDER BY eh.country ASC 
    """)
    List<Object[]> findCountriesWithCountByUserId(@Param("userId") Long userId);

    List<FavoriteExhibit> user(User user);

    @Query("""
        SELECT f.exhibit.exhibitId
        FROM FavoriteExhibit f
        WHERE f.user.userId = :userId
        """)
    Set<Long> findExhibitIdsByUserId(@Param("userId") Long userId);
}
