package org.atdev.artrip.domain.favorite;

import jakarta.persistence.*;
import lombok.*;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.exhibit.Exhibit;

import java.time.LocalDate;

@Entity
@Table(name = "favorite")
@Getter
@NoArgsConstructor
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long favoriteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean status = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhibit_id", nullable = false)
    private Exhibit exhibit;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    public static Favorite of(Long favoriteId, User user, Exhibit exhibit, boolean status, LocalDate createdAt) {
        Favorite favorite = new Favorite();
        favorite.favoriteId = favoriteId;
        favorite.user = user;
        favorite.exhibit = exhibit;
        favorite.status = status;
        favorite.createdAt = createdAt;
        return favorite;

    }

    private Favorite(User user, Exhibit exhibit, boolean status, LocalDate createdAt) {
        this.user = user;
        this.exhibit = exhibit;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Favorite create(User user, Exhibit exhibit) {
        return new Favorite(
                user,
                exhibit,
                true,
                LocalDate.now()
        );
    }

    public void deactivate() {
        this.status = false;
    }

    public void activate() {
        this.status = true;
    }
}
