package org.atdev.artrip.entity.user;

import jakarta.persistence.*;
import lombok.*;
import org.atdev.artrip.entity.exhibit.Exhibit;

import java.sql.Timestamp;

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
    @Column(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "exhibit_id", nullable = false)
    private Exhibit exhibit;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;
}
