package org.atdev.artrip.repository;

import org.atdev.artrip.domain.exhibit.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long> {
}
