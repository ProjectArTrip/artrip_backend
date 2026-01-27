package org.atdev.artrip.domain.exhibitHall;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "exhibit_hall")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExhibitHall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exhibit_hall_id")
    private Long exhibitHallId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "country")
    private String country;

    @Column(name = "region")
    private String region;

    @Column(name = "address")
    private String address;

    @Column(name = "opening_hours")
    private String openingHours;

    @Column(name = "closed_days", length = 255)
    private String closedDays;

    @Column(name = "phone")
    private String phone;

    @Column(name = "homepage_url")
    private String homepageUrl;

    @Column(name = "is_domestic")
    private Boolean isDomestic;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "longitude")
    private BigDecimal longitude;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static ExhibitHall of(Long exhibitHallId, String name, String country, String region, Boolean isDomestic) {
        ExhibitHall exhibitHall = new ExhibitHall();
        exhibitHall.exhibitHallId = exhibitHallId;
        exhibitHall.name = name;
        exhibitHall.country = country;
        exhibitHall.region = region;
        exhibitHall.isDomestic = isDomestic;
        return exhibitHall;
    }
}
