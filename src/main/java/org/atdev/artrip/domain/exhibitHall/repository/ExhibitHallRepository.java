package org.atdev.artrip.domain.exhibitHall.repository;

import org.atdev.artrip.domain.exhibitHall.data.ExhibitHall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitHallRepository extends JpaRepository<ExhibitHall, Long> {

    Page<ExhibitHall> findByNameContaining(String name, Pageable pageable);

}
