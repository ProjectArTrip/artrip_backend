package org.atdev.artrip.domain.exhibitReport;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.atdev.artrip.constants.ExhibitReportStatus;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.exhibit.Exhibit;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "exhibit_report")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExhibitReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exhibit_report_id")
    private Long exhibitReportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "title", nullable = false, length = 20)
    private String title;

    @Column(name = "country", nullable = false)
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ExhibitReportStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhibit_id", nullable = true)
    private Exhibit exhibit;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


    public static ExhibitReport create(User user, String title, String country) {
        ExhibitReport report = new ExhibitReport();
        report.user = user;
        report.title = title;
        report.country = country;
        report.status = ExhibitReportStatus.PENDING;
        report.createdAt = LocalDateTime.now();
        return report;
    }

    public static ExhibitReport of(Long exhibitReportId, User user, String title, String country, ExhibitReportStatus status, Exhibit exhibit, LocalDateTime createdAt) {
        ExhibitReport report = new ExhibitReport();
        report.exhibitReportId = exhibitReportId;
        report.user = user;
        report.title = title;
        report.country = country;
        report.status = status;
        report.exhibit = exhibit;
        report.createdAt = createdAt;

        return report;
    }

    public void markRegistered(Exhibit exhibit) {
        this.status = ExhibitReportStatus.REGISTERED;
        this.exhibit = exhibit;
    }

    public boolean isRegistered() {
        return status == ExhibitReportStatus.REGISTERED;
    }
}
