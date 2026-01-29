package org.atdev.artrip.domain.curation;

import jakarta.persistence.*;
import lombok.*;
import org.atdev.artrip.constants.CurationType;
import org.atdev.artrip.domain.exhibit.Exhibit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "curation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Curation {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "curation_id")
        private Long curationId;

        @Column(name = "title",nullable = false)
        private String title;

        @Column(name = "sub_description")
        private String subDescription;

        @Enumerated(EnumType.STRING)
        @Column(name = "curation_type", nullable = false)
        private CurationType curationType;

        @Builder.Default
        @Column(name = "is_active")
        private boolean isActive = true;

        @Builder.Default
        @ManyToMany
        @JoinTable(
                name = "curation_exhibit",
                joinColumns = @JoinColumn(name = "curation_id"),
                inverseJoinColumns = @JoinColumn(name = "exhibit_id")
        )
        private List<Exhibit> exhibits = new ArrayList<>();
}
