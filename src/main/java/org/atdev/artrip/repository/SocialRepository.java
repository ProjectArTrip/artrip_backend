package org.atdev.artrip.repository;

import org.atdev.artrip.constants.Provider;
import org.atdev.artrip.domain.auth.SocialAccounts;
import org.atdev.artrip.domain.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocialRepository extends JpaRepository<SocialAccounts,Long> {

    Optional<SocialAccounts> findByProviderAndProviderId(Provider provider, String providerId);

    List<SocialAccounts> findAllByUser(User user);
}
