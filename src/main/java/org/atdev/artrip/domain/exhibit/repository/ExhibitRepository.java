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


    @Query(value = "SELECT * FROM exhibit ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Exhibit> findRandomExhibits(@Param("limit") int limit);


    @Query(value = """
    SELECT e.*
    FROM exhibit e
    JOIN exhibit_keyword ek ON e.exhibit_id = ek.exhibit_id
    JOIN keyword k ON ek.keyword_id = k.keyword_id
    WHERE k.keyword_type = 'GENRE'
      AND k.keyword_name = :genre
      AND e.end_date >= NOW()
    ORDER BY RAND()
    LIMIT :limit
    """, nativeQuery = true)
    List<Exhibit> findThemeExhibits(@Param("genre") String genre, @Param("limit") int limit);

    @Query(value = """
        SELECT DISTINCT k.keyword_name
        FROM keyword k
        WHERE k.keyword_type = 'GENRE'
        ORDER BY k.keyword_name ASC
        """, nativeQuery = true)
    List<String> findAllGenres();

    List<Exhibit> findByUpdatedAtAfter(LocalDateTime time);

    @Query("SELECT DISTINCT e FROM Exhibit e LEFT JOIN FETCH e.keywords WHERE e.exhibitId = :id")
    Optional<Exhibit> findByIdWithKeywords(@Param("id") Long id);

    @Query("SELECT DISTINCT e FROM Exhibit e LEFT JOIN FETCH e.keywords")
    List<Exhibit> findAllWithKeywords();

}
