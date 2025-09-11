package org.atdev.artrip.entity.exhibit;

import jakarta.persistence.*;
import lombok.*;
import org.atdev.artrip.entity.Enum.Genre;
import org.atdev.artrip.entity.Enum.Status;

import java.math.BigDecimal;
import java.sql.Timestamp;

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

    @Column(name = "exhibit_hall_id")
    private Long exhibitHallId;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "start_date")
    private Timestamp startDate;

    @Column(name = "end_date")
    private Timestamp endDate;

    @Enumerated(EnumType.STRING) // DB가 CHAR/VARCHAR이면 STRING
    @Column(name = "status", nullable = false)
    private Status status; // Java enum

    @Column(name = "poster_url")
    private String posterUrl;

    @Column(name = "ticket_url")
    private String ticketUrl;

    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "genre", nullable = false)
    private Genre genre;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "longitude")
    private BigDecimal longitude;
}
