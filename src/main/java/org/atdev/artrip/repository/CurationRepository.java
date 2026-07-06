package org.atdev.artrip.repository;

import org.atdev.artrip.domain.curation.Curation;
import org.atdev.artrip.domain.curation.CurationExhibit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CurationRepository extends JpaRepository<Curation, Long>, CurationRepositoryCustom {

    @Query(value = """
            select ce
            from CurationExhibit ce
                join fetch ce.exhibit e
                join fetch e.exhibitHall
            where ce.curation.curationId = :curationId
            order by ce.displayOrder asc
            """)
    Slice<CurationExhibit> findExhibitsByCurationId(@Param("curationId") Long curationId, Pageable pageable);

    @Query(value = """
            select ce
            from CurationExhibit ce
                join fetch ce.exhibit e
                join fetch e.exhibitHall
            where ce.curation.curationId = :curationId
                and ce.displayOrder > :cursor
            order by ce.displayOrder asc
            """)
    Slice<CurationExhibit> findExhibitsByCurationIdAndCursor(@Param("curationId") Long curationId, @Param("cursor") int cursor, Pageable pageable);

    @Query("""
            select distinct c.curationId
            from Curation c
            join c.curationExhibits ce
            join ce.exhibit e
            join e.exhibitHall eh
            where c.active = true
                and c.visibleFrom <= :today
                and c.visibleTo   >= :today
                and (:domestic is null or eh.isDomestic = :domestic)
                and (:country  is null or eh.country    = :country)
            group by c.curationId
            having count(ce.curationExhibitId) >= :minCount
            """)
    List<Long> findVisibleCurationIds(
            @Param("today") LocalDate today,
            @Param("domestic") Boolean domestic,
            @Param("country") String country,
            @Param("minCount") int minCount
    );

    @Modifying
    @Query("""
            delete from CurationExhibit ce where ce.exhibit.exhibitId = :exhibitId
            """)
    void deleteCurationExhibitByExhibitId(@Param("exhibitId") Long exhibitId);

    @Query("""
            select distinct c
            from Curation c
            left join fetch c.curationExhibits ce
            left join fetch ce.exhibit e
            left join fetch e.exhibitHall eh
                where c.curationId = :curationId
            and (:country is null or eh.country = :country)
            """)
    Optional<Curation> findByIdWithExhibits(
            @Param("curationId") Long curationId,
            @Param("country") String country
    );
}
