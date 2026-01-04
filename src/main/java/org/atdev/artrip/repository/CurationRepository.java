package org.atdev.artrip.repository;

import org.atdev.artrip.domain.curation.Curation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurationRepository extends JpaRepository<Curation, Long> {
}
