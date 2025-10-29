package org.atdev.artrip.domain.exhibit.repository;

import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.exhibitHall.data.ExhibitHall;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ExhibitRepository extends JpaRepository<Exhibit, Long>{


    @Query(value = """
    SELECT e.*
    FROM exhibit e
    JOIN exhibit_hall h ON e.exhibit_hall_id = h.exhibit_hall_id
    WHERE (:isDomestic IS NULL OR h.is_domestic = :isDomestic)
    ORDER BY RAND()
    LIMIT :limit
    """, nativeQuery = true)
    List<Exhibit> findRandomExhibits(@Param("limit") int limit, @Param("isDomestic") Boolean isDomestic);


    @Query(value = """
    SELECT e.*
    FROM exhibit e
    JOIN exhibit_keyword ek ON e.exhibit_id = ek.exhibit_id
    JOIN keyword k ON ek.keyword_id = k.keyword_id
    JOIN exhibit_hall h ON e.exhibit_hall_id = h.exhibit_hall_id
    WHERE k.type = 'GENRE'
      AND k.name = :genre
      AND e.end_date >= NOW()
      AND (:isDomestic IS NULL OR h.is_domestic = :isDomestic)
    ORDER BY RAND()
    LIMIT :limit
    """, nativeQuery = true)
    List<Exhibit> findThemeExhibits(@Param("genre") String genre, @Param("limit") int limit, @Param("isDomestic") Boolean isDomestic);

    @Query(value = """
    SELECT e.*
    FROM exhibit e
    JOIN exhibit_keyword ek ON e.exhibit_id = ek.exhibit_id
    JOIN keyword k ON ek.keyword_id = k.keyword_id
    JOIN exhibit_hall h ON e.exhibit_hall_id = h.exhibit_hall_id
    WHERE k.type = 'GENRE'
      AND k.name = :genre
      AND e.end_date >= NOW()
      AND (:isDomestic IS NULL OR h.is_domestic = :isDomestic)
    """, nativeQuery = true)
    List<Exhibit> findAllByGenreAndDomestic(
            @Param("genre") String genre,
            @Param("isDomestic") Boolean isDomestic
    );

    @Query(value = """
        SELECT DISTINCT k.name
        FROM keyword k
        WHERE k.type = 'GENRE'
        ORDER BY k.name ASC
        """, nativeQuery = true)
    List<String> findAllGenres();


    @Query(value = """
    SELECT e.* 
    FROM exhibit e
    JOIN exhibit_keyword ek ON e.exhibit_id = ek.exhibit_id
    JOIN keyword k ON ek.keyword_id = k.keyword_id
    JOIN exhibit_hall h ON e.exhibit_hall_id = h.exhibit_hall_id
    WHERE e.end_date >= NOW()
      AND (:isDomestic IS NULL OR h.is_domestic = :isDomestic)
      AND ((k.type = 'GENRE' AND k.name IN (:genres))
           OR (k.type = 'STYLE' AND k.name IN (:styles)))
    ORDER BY RAND()
    LIMIT :limit
    """, nativeQuery = true)
    List<Exhibit> findRandomByKeywords(
            @Param("genres") Set<String> genres,
            @Param("styles") Set<String> styles,
            @Param("limit") int limit,
            @Param("isDomestic") Boolean isDomestic
    );

    @Query(value = """
    SELECT e.* 
    FROM exhibit e
    JOIN exhibit_keyword ek ON e.exhibit_id = ek.exhibit_id
    JOIN keyword k ON ek.keyword_id = k.keyword_id
    JOIN exhibit_hall h ON e.exhibit_hall_id = h.exhibit_hall_id
    WHERE e.end_date >= NOW()
      AND (:isDomestic IS NULL OR h.is_domestic = :isDomestic)
      AND ((k.type = 'GENRE' AND k.name IN (:genres))
           OR (k.type = 'STYLE' AND k.name IN (:styles)))
    """, nativeQuery = true)
    List<Exhibit> findAllByKeywords(
            @Param("genres") Set<String> genres,
            @Param("styles") Set<String> styles,
            @Param("isDomestic") Boolean isDomestic
    );

    @Modifying
    @Query(value = """
    UPDATE exhibit
    SET status = 'ENDING_SOON'
    WHERE status = 'ONGOING'
      AND end_date <= NOW() + INTERVAL 3 DAY
      AND end_date > NOW()
""", nativeQuery = true)
    int updateEndingSoonStatus();

    @Modifying
    @Query(value = """
    UPDATE exhibit
    SET status = 'FINISHED'
    WHERE status IN ('ONGOING', 'ENDING_SOON')
      AND end_date <= NOW()
""", nativeQuery = true)
    int updateFinishedStatus();


    @Query(value = """
        SELECT e.* 
        FROM exhibit e
        JOIN exhibit_hall h ON e.exhibit_hall_id = h.exhibit_hall_id
        WHERE :date BETWEEN e.start_date AND e.end_date
        AND (:isDomestic IS NULL OR h.is_domestic = :isDomestic)
        ORDER BY RAND()
        LIMIT :limit
        """, nativeQuery = true)
    List<Exhibit> findRandomExhibitsByDate(@Param("isDomestic") Boolean isDomestic,
                                           @Param("date") LocalDate date,
                                           @Param("limit") int limit);

    @Query(value = """
        SELECT e.* 
        FROM exhibit e
        JOIN exhibit_hall h ON e.exhibit_hall_id = h.exhibit_hall_id
        WHERE :date BETWEEN e.start_date AND e.end_date
        AND (:isDomestic IS NULL OR h.is_domestic = :isDomestic)
        """, nativeQuery = true)
    List<Exhibit> findAllByDate(@Param("isDomestic") Boolean isDomestic,
                                @Param("date") LocalDate date);

    @Query("SELECT DISTINCT e FROM Exhibit e LEFT JOIN FETCH e.keywords WHERE e.exhibitId = :id")
    Optional<Exhibit> findByIdWithKeywords(@Param("id") Long id);

    @Query("SELECT DISTINCT e FROM Exhibit e LEFT JOIN FETCH e.keywords")
    List<Exhibit> findAllWithKeywords();

    Page<Exhibit> findByDescriptionContaining(String description, Pageable pageable);

    long countByExhibitHall_ExhibitHallId(Long exhibitHallId);


    @Query(value = """
    SELECT e.*
    FROM exhibit e
    JOIN exhibit_hall h ON e.exhibit_hall_id = h.exhibit_hall_id
    WHERE h.country = :country
    ORDER BY RAND()
    LIMIT :limit
""", nativeQuery = true)
    List<Exhibit> findRandomByCountry(@Param("country") String country, @Param("limit") int limit);

    @Query(value = """
    SELECT e.*
    FROM exhibit e
    JOIN exhibit_hall h ON e.exhibit_hall_id = h.exhibit_hall_id
    WHERE h.region = :region
    ORDER BY RAND()
    LIMIT :limit
""", nativeQuery = true)
    List<Exhibit> findRandomByRegion(@Param("region") String region, @Param("limit") int limit);


}
