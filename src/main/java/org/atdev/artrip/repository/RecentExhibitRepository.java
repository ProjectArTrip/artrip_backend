package org.atdev.artrip.repository;

import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.domain.exhibit.RecentExhibit;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RecentExhibitRepository extends JpaRepository<RecentExhibit, Long> {

    Optional<RecentExhibit> findByUserAndExhibit(User user, Exhibit exhibit);

    @EntityGraph(attributePaths = {"exhibit"})
    List<RecentExhibit> findTop20ByUserOrderByViewAtDesc(User user);

    @Modifying
    @Query("DELETE FROM RecentExhibit r WHERE r.viewAt < :limit")
    void deleteByViewAtBefore(LocalDateTime limit);
}
