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
        SELECT * FROM exhibit
        WHERE (:genre = '전체' OR genre = :genre)
        ORDER BY RAND()
        LIMIT :limit
        """, nativeQuery = true)
    List<Exhibit> findThemeExhibits(@Param("genre") String genre, @Param("limit") int limit);

    List<Exhibit> findByUpdatedAtAfter(LocalDateTime time);

    @Query("SELECT DISTINCT e FROM Exhibit e LEFT JOIN FETCH e.keywords WHERE e.exhibitId = :id")
    Optional<Exhibit> findByIdWithKeywords(@Param("id") Long id);

    @Query("SELECT DISTINCT e FROM Exhibit e LEFT JOIN FETCH e.keywords")
    List<Exhibit> findAllWithKeywords();

}
