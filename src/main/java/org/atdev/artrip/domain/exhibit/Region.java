package org.atdev.artrip.domain.exhibit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Region {

    @Id
    @Column(name = "region_name")
    private String name;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;
}