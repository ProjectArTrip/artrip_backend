package org.atdev.artrip.domain.favortie.data;

import jakarta.persistence.*;
import lombok.*;
import org.atdev.artrip.domain.auth.data.User;
import org.atdev.artrip.domain.exhibit.data.Exhibit;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorite_exhibit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteExhibit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long favoriteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @Builder.Default
    private boolean status = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhibit_id", nullable = false)
    private Exhibit exhibit;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
