package org.atdev.artrip.domain.exhibit.repository;

import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    List<Exhibit> findByUpdatedAtAfter(LocalDateTime time);

    @Query("SELECT DISTINCT e FROM Exhibit e LEFT JOIN FETCH e.keywords WHERE e.exhibitId = :id")
    Optional<Exhibit> findByIdWithKeywords(@Param("id") Long id);

    @Query("SELECT DISTINCT e FROM Exhibit e LEFT JOIN FETCH e.keywords")
    List<Exhibit> findAllWithKeywords();

}
