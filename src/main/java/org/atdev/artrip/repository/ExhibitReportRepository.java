package org.atdev.artrip.repository;

import org.atdev.artrip.domain.exhibitReport.ExhibitReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExhibitReportRepository extends JpaRepository<ExhibitReport, Long> {

    @EntityGraph(attributePaths = {"user"})
    Page<ExhibitReport> findAll(Pageable pageable);
}
