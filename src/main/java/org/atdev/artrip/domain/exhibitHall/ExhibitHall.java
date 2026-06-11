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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exhibit_hall_seq_gen")
    @SequenceGenerator(name = "exhibit_hall_seq_gen", sequenceName = "exhibit_hall_seq", allocationSize = 50)
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

    @Column(name = "opening_hours", length = 500)
    private String openingHours;

    @Column(name = "closed_days", length = 255)
    private String closedDays;

    @Column(name = "phone")
    private String phone;

    @Column(name = "homepage_url", length = 1024)
    private String homepageUrl;

    @Column(name = "is_domestic")
    private Boolean isDomestic;

    @Column(name = "latitude", nullable = false)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false)
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

    public void updateInfo(String name, String country, String region, String address, String openingHours, String phone,
                            BigDecimal latitude, BigDecimal longitude, Boolean isDomestic) {
        this.name = name;
        this.country = country;
        this.region = region;
        this.address = address;
        this.openingHours = openingHours;
        this.phone = phone != null ? phone : this.phone;
        this.latitude = latitude != null ? latitude : this.latitude;
        this.longitude = longitude != null ? longitude : this.longitude;
        this.isDomestic = isDomestic;
    }

    public static ExhibitHall create(String name, String country, String region, String address, String openingHours, String phone,
                                    BigDecimal latitude, BigDecimal longitude, Boolean isDomestic) {
        ExhibitHall hall = new ExhibitHall();
        hall.name = name;
        hall.country = country;
        hall.region = region;
        hall.address = address;
        hall.openingHours = openingHours;
        hall.phone = phone;
        hall.latitude = latitude;
        hall.longitude = longitude;
        hall.isDomestic = isDomestic;
        return hall;
    }
}
