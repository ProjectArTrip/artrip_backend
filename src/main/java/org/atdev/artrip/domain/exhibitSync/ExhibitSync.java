package org.atdev.artrip.domain.exhibitSync;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.atdev.artrip.domain.exhibit.Exhibit;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "exhibit_sync",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_exhibit_sync_source_external",
                columnNames = {"source", "external_id"}
        ))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExhibitSync {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exhibit_sync_id")
    private Long exhibitSyncId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhibit_id", nullable = false)
    private Exhibit exhibit;

    @Column(name = "source", nullable = false, length = 50)
    private String source;

    @Column(name = "external_id", nullable = false, length = 100)
    private String externalId;

    @Column(name = "content_hash", nullable = false, length = 64)
    private String contentHash;

    @Column(name = "synced_at", nullable = false)
    private LocalDateTime syncedAt;

    public static ExhibitSync create(Exhibit exhibit, String source, String externalId, String contentHash) {
        ExhibitSync sync = new ExhibitSync();

        sync.exhibit = exhibit;
        sync.source = source;
        sync.externalId = externalId;
        sync.contentHash = contentHash;
        sync.syncedAt = LocalDateTime.now();
        return sync;
    }

    public void updateHash(String contentHash) {
        this.contentHash = contentHash;
        this.syncedAt = LocalDateTime.now();
    }

    public boolean isSameContent(String contentHash) {
        return this.contentHash.equals(contentHash);
    }

}
