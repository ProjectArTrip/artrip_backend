package org.atdev.artrip.domain.curation;

import jakarta.persistence.*;
import lombok.Getter;
import org.atdev.artrip.domain.exhibit.Exhibit;

@Getter
@Entity
@Table(name = "curation_exhibit")
public class CurationExhibit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long curationExhibitId;

    @JoinColumn(name = "curation_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Curation curation;

    @JoinColumn(name = "exhibit_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Exhibit exhibit;

    @Column(nullable = false)
    private int displayOrder;

    protected CurationExhibit() {}

    private CurationExhibit(Curation curation, Exhibit exhibit, int displayOrder){
        this.curation = curation;
        this.exhibit = exhibit;
        this.displayOrder = displayOrder;
    }

    public static CurationExhibit of(Curation curation, Exhibit exhibit, int displayOrder) {
        return new CurationExhibit(curation, exhibit, displayOrder);
    }
}
