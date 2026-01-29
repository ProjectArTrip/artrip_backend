package org.atdev.artrip.repository;

import org.atdev.artrip.domain.auth.SocialAccounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialRepository extends JpaRepository<SocialAccounts,Long> {
}
