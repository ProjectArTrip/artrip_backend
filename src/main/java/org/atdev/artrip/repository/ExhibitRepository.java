package org.atdev.artrip.repository;

import org.atdev.artrip.domain.exhibit.Exhibit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExhibitRepository extends JpaRepository<Exhibit, Long>,ExhibitRepositoryCustom{


    @Query(value = """
        SELECT DISTINCT k.name
        FROM keyword k
        WHERE k.type = 'GENRE'
        ORDER BY k.name ASC
        """, nativeQuery = true)
    List<String> findAllGenres();


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

    @Query("SELECT DISTINCT e FROM Exhibit e LEFT JOIN FETCH e.keywords WHERE e.exhibitId = :id")
    Optional<Exhibit> findByIdWithKeywords(@Param("id") Long id);

    Page<Exhibit> findByDescriptionContaining(String description, Pageable pageable);

    long countByExhibitHall_ExhibitHallId(Long exhibitHallId);

    Optional<Exhibit> findByTitleAndStartDate(String title, LocalDate startDate);

    // 패치조인 전시홀 전시관
    @Query("select e from Exhibit e join fetch e.exhibitHall where e.exhibitId in :ids")
    List<Exhibit> findAllByIdWithHall(@Param("ids") List<Long> ids);

}
