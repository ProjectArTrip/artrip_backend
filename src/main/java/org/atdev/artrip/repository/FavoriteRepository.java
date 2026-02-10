package org.atdev.artrip.repository;

import org.atdev.artrip.constants.Status;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;
import org.atdev.artrip.domain.favorite.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    @Query("""
    SELECT COUNT(f) > 0
        FROM Favorite f
        WHERE f.user.userId = :userId
        AND f.exhibit.exhibitId = :exhibitId
        And f.status = true 
    """)
    boolean existsActive(@Param("userId") Long userId, @Param("exhibitId") Long exhibitId);

    @Query("""
    SELECT f
    FROM Favorite f
    INNER JOIN FETCH f.exhibit e
    INNER JOIN FETCH e.exhibitHall
    WHERE f.user.userId = :userId
    AND f.status = true
    AND e.status != :status
    AND (:cursor IS NULL OR f.favoriteId < :cursor)
    ORDER BY f.createdAt DESC
    """)
    Slice<Favorite> findAllActive(
            @Param("userId") Long userId,
            @Param("cursor") Long cursor,
            @Param("status") Status status,
            Pageable pageable);

    @Query("""
            SELECT f.exhibit.exhibitId
            FROM Favorite f
            WHERE f.user.userId = :userId AND f.status = true
            """)
    Set<Long> findActiveExhibitIds(@Param("userId") Long userId);

    @Query("""
    SELECT f
    FROM Favorite f
    INNER JOIN FETCH f.exhibit e
    INNER JOIN FETCH e.exhibitHall eh
    WHERE f.user.userId = :userId
    AND f.status = true
    AND e.status != :status
    AND (:isDomestic IS NULL OR eh.isDomestic = :isDomestic)
    AND (:country IS NULL OR eh.country LIKE %:country%)
    AND (:region IS NULL OR eh.region LIKE %:region%)
    AND (:cursor IS NULL OR f.favoriteId < :cursor)
    """)
    Slice<Favorite> findFavorites(
            @Param("userId") Long userId,
            @Param("isDomestic") Boolean isDomestic,
            @Param("country") String country,
            @Param("region") String region,
            @Param("cursor") Long curosr,
            @Param("status") Status status,
            Pageable pageable
    );
}

