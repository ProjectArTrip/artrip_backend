package org.atdev.artrip.repository;

import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.favorite.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long>, FavoriteRepositoryCustom {

    @Query(
            """
            select f
            from Favorite f
            where f.user.userId = :userId
            and f.exhibit.exhibitId = :exhibitId
            """
    )
    Optional<Favorite> findFavorite(@Param("userId") Long userId, @Param("exhibitId") Long exhibitId);

    @Modifying
    @Query("DELETE FROM Favorite f WHERE f.user = :user")
    void deleteAllByUser(@Param("user") User user);
}
