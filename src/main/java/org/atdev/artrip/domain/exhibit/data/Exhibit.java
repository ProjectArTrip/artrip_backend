package org.atdev.artrip.domain.exhibit.data;

import jakarta.persistence.*;
import lombok.*;
import org.atdev.artrip.domain.Enum.Status;

import org.atdev.artrip.domain.keyword.data.Keyword;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "exhibit", schema = "art_dev")
@EntityListeners(ExhibitEntityListener.class)
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
    private Status status;

    @Column(name = "poster_url")
    private String posterUrl;

    @Column(name = "ticket_url")
    private String ticketUrl;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "longitude")
    private BigDecimal longitude;


    @ManyToMany
    @Builder.Default
    @JoinTable(
            name = "exhibit_keyword",
            joinColumns = @JoinColumn(name = "exhibit_id"),
            inverseJoinColumns = @JoinColumn(name = "keyword_id")
    )
    private Set<Keyword> keywords = new HashSet<>();
}
