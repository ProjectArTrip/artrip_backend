package org.atdev.artrip.elastic.repository;

import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EsExhibitRepository extends JpaRepository<Exhibit, Long> {

}
