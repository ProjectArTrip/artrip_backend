package org.atdev.artrip.domain;

import jakarta.persistence.*;
import lombok.*;
import org.atdev.artrip.domain.exhibit.data.Exhibit;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "favorite_exhibit", schema = "art_dev")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhibit_id", nullable = false)
    private Exhibit exhibit;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
