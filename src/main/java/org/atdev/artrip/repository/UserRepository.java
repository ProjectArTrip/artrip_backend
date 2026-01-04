package org.atdev.artrip.repository;

import org.atdev.artrip.constants.Provider;
import org.atdev.artrip.domain.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

//    Optional<User> findBySocialAccountsProviderAndSocialAccountsProviderId(Provider provider, String provideId);

    Optional<User> findByEmail(String email);

    Optional<User> findByUserId(Long userId);

    @Query("SELECT u FROM User u JOIN FETCH u.socialAccounts sa WHERE sa.provider = :provider AND sa.providerId = :providerId")
    Optional<User> findBySocialAccountsProviderAndProviderId(@Param("provider") Provider provider, @Param("providerId") String providerId);

    boolean existsByNickName(String nickname);
}
