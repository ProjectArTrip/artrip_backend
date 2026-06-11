package org.atdev.artrip.repository;

import org.atdev.artrip.domain.exhibitHall.ExhibitHall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ExhibitHallRepository extends JpaRepository<ExhibitHall, Long> {

    Page<ExhibitHall> findByNameContaining(String name, Pageable pageable);

    @Query("SELECT DISTINCT e.country FROM ExhibitHall e WHERE e.country <> '한국'")
    List<String> findAllOverseasCountries();

    Optional<ExhibitHall> findByName(String placeName);

    @Query("select h from ExhibitHall h where h.name in :names")
    List<ExhibitHall> findByNameIn(@Param("names") Set<String> names);
}
