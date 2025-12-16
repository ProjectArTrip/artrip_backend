package org.atdev.artrip.domain.auth.repository;

import org.atdev.artrip.domain.auth.data.SocialAccounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialRepository extends JpaRepository<SocialAccounts,Long> {
}
