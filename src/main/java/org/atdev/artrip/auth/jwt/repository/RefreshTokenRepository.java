package org.atdev.artrip.auth.jwt.repository;

import org.atdev.artrip.auth.jwt.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    Optional<RefreshToken> findByUsername(String username);//username은 이메일이야

    void deleteByUsername(String username);



}
