package org.atdev.artrip.repository;

import org.atdev.artrip.constants.Status;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.repository.dto.ExhibitMarkerDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

    @Query("SELECT DISTINCT e FROM Exhibit e LEFT JOIN FETCH e.keywords")
    List<Exhibit> findAllWithKeywords();

    Page<Exhibit> findByDescriptionContaining(String description, Pageable pageable);

    long countByExhibitHall_ExhibitHallId(Long exhibitHallId);

    Optional<Exhibit> findByTitleAndStartDate(String title, LocalDate startDate);



    @Query("select e from Exhibit e join fetch e.exhibitHall where e.exhibitId = :id")
    Optional<Exhibit> findByIdWithHall(@Param("id") Long id);


//    @Query("SELECT new org.atdev.artrip.repository.dto.ExhibitMarkerDto(e.exhibitId, h.longitude, h.latitude) " +
//            "FROM Exhibit e " + "JOIN e.exhibitHall h")
//    List<ExhibitMarkerDto> findAllLocationsForCache();

    @Query("select e from Exhibit e where e.exhibitId in :ids order by e.startDate desc,e.exhibitId desc")
    Slice<Exhibit> findByIdInOrderByIdDesc(@Param("ids") List<Long> ids, Pageable pageable);

    @Query("select e from Exhibit e " + "where e.exhibitId in :ids " +
            "and (e.startDate < :cursorDate or (e.startDate = :cursorDate and e.exhibitId < :cursorId)) " +
            "order by e.startDate desc, e.exhibitId desc")
    Slice<Exhibit> findByIdInAndIdLessThanOrderByIdDesc(@Param("ids") List<Long> ids,
                                                        @Param("cursorDate") LocalDate cursorDate,
                                                        @Param("cursor") Long cursor,
                                                        Pageable pageable);


    @Query("""
        SELECT new org.atdev.artrip.repository.dto.ExhibitMarkerDto(
            e.exhibitId,
            h.latitude,
            h.longitude
        )
        FROM Exhibit e
        JOIN e.exhibitHall h
        WHERE e.status IN :statuses
    """)
    List<ExhibitMarkerDto> findMarkersByStatus(@Param("statuses") List<Status> statuses);


    @Query("""
    SELECT COUNT(e) AS count, MAX(e.updatedAt) AS lastUpdated
    FROM Exhibit e
    WHERE e.status IN :statuses
    """)
    ExhibitMarkerMeta findMarkerMeta(@Param("statuses") List<Status> statuses);

//    LocalDateTime findMarkerLastUpdated(List<Status> statuses);
}
