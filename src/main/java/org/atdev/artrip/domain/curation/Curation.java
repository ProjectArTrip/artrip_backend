package org.atdev.artrip.domain.curation;

import jakarta.persistence.*;
import lombok.Getter;
import org.atdev.artrip.constants.RefreshCycle;
import org.atdev.artrip.constants.SortType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "curation")
@EntityListeners(AuditingEntityListener.class)
public class Curation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long curationId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 200)
    private String subtitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RefreshCycle refreshCycle;

    @Column(nullable = false)
    private LocalDate visibleFrom;

    @Column(nullable = false)
    private LocalDate visibleTo;

    @Column(nullable = false)
    private boolean active;

    @Column
    private int displayCount;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDate createdAt;

    @LastModifiedDate
    @Column
    private LocalDate updatedAt;

    @Enumerated(EnumType.STRING)
    @Column
    private SortType sortType;

    @OneToMany(mappedBy = "curation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CurationExhibit> curationExhibits = new ArrayList<>();

    protected Curation() {}

    public Curation(String title, String subtitle, RefreshCycle refreshCycle, LocalDate visibleFrom, LocalDate visibleTo, boolean active, SortType sortType, int displayCount) {
        this.title = title;
        this.subtitle = subtitle;
        this.refreshCycle = refreshCycle;
        this.visibleFrom = visibleFrom;
        this.visibleTo = visibleTo;
        this.active = active;
        this.sortType = sortType;
        this.displayCount = displayCount;
    }

    public static Curation of(String title, String subtitle, RefreshCycle refreshCycle, LocalDate visibleFrom, LocalDate visibleTo, boolean active, SortType sortType, int displayCount) {
        return new Curation(
                title,
                subtitle,
                refreshCycle,
                visibleFrom,
                visibleTo,
                active,
                sortType,
                displayCount
        );
    }

    public boolean isVisibleOn(LocalDate today) {
        return active && !visibleFrom.isAfter(today) && !visibleTo.isBefore(today);
    }
}
