package org.atdev.artrip.repository;

import org.atdev.artrip.domain.maintenance.Maintenance;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
    @Query("""
            select m
            from Maintenance m
            order by m.maintenanceId desc
            """)
    List<Maintenance> findLatest(Pageable pageable);
}
