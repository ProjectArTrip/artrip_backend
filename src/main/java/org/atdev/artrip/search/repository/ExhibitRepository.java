package org.atdev.artrip.search.repository;

import org.atdev.artrip.entity.exhibit.Exhibit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitRepository extends JpaRepository<Exhibit, Long> {

}
