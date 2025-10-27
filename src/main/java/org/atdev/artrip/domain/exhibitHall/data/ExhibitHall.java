package org.atdev.artrip.domain.exhibitHall.data;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "exhibit_hall", schema = "art_dev")
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
    private Boolean isDomestic;  // Byte → Boolean으로 변경, JPA에서 매핑 가능
    // true = 국내, false = 해외

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
