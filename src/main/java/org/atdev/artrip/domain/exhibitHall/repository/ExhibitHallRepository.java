package org.atdev.artrip.domain.exhibitHall.repository;

import org.atdev.artrip.domain.exhibitHall.data.ExhibitHall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExhibitHallRepository extends JpaRepository<ExhibitHall, Long> {

    Page<ExhibitHall> findByNameContaining(String name, Pageable pageable);

    @Query("SELECT DISTINCT e.country FROM ExhibitHall e WHERE e.country <> '한국'")
    List<String> findAllOverseasCountries();

    @Query("SELECT DISTINCT e.region FROM ExhibitHall e WHERE e.country = '한국'")
    List<String> findAllDomesticRegions();


    Optional<ExhibitHall> findByName(String placeName);
}
