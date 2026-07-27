package org.atdev.artrip.repository;

import org.atdev.artrip.domain.exhibitSync.ExhibitSync;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ExhibitSyncRepository extends JpaRepository<ExhibitSync, Long> {

    @EntityGraph(attributePaths = {"exhibit"})
    List<ExhibitSync> findBySourceAndExternalIdIn(String source, Collection<String> externalIds);
}
