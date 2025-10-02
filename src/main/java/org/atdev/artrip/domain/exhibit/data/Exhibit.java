package org.atdev.artrip.domain.exhibit.data;

import jakarta.persistence.*;
import lombok.*;
import org.atdev.artrip.domain.Enum.Genre;
import org.atdev.artrip.domain.Enum.Status;
import org.atdev.artrip.domain.ExhibitHall;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "exhibit", schema = "art_dev")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exhibit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exhibit_id")
    private Long exhibitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhibit_hall_id")
    private ExhibitHall exhibitHall;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING) // DB가 CHAR/VARCHAR이면 STRING
    @Column(name = "status", nullable = false)
    private Status status; // Java enum

    @Column(name = "poster_url")
    private String posterUrl;

    @Column(name = "ticket_url")
    private String ticketUrl;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "genre", nullable = false)
    private Genre genre;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "longitude")
    private BigDecimal longitude;
}
