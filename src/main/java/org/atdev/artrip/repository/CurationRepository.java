package org.atdev.artrip.repository;

import org.atdev.artrip.constants.Country;
import org.atdev.artrip.domain.curation.Curation;
import org.atdev.artrip.domain.curation.CurationExhibit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CurationRepository extends JpaRepository<Curation, Long> {

    // 오늘 기준으로 노출 가능한 큐레이션 조회
    @Query(value = """
            select distinct c
            from Curation c
                left join fetch c.curationExhibits ce
                left join fetch ce.exhibit e
                left join fetch e.exhibitHall
            where c.active = true
                and c.visibleFrom <= :today
                and c.visibleTo >= :today
            """)
    List<Curation> findVisibleCurations(@Param("today") LocalDate today);

    @Query(value = """
            select distinct c
                from Curation c
                    join fetch c.curationExhibits ce
                    join fetch ce.exhibit e
                    join fetch e.exhibitHall eh
                where c.active = true
                    and c.visibleFrom <= :today
                    and c.visibleTo >= :today
                    and eh.country = :country
            """)
    List<Curation> findVisibleCurationsByCountry(@Param("country") Country country, @Param("today") LocalDate today);

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
}
