package org.atdev.artrip.domain.curation.repository;

import org.atdev.artrip.domain.curation.data.Curation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurationRepository extends JpaRepository<Curation, Long> {
}
