package org.atdev.artrip.entity.user;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;

@Entity
@Table(name = "search_history", schema = "art_dev")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "search_history_id")
    private Long searchHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "content", nullable = false)
    private String content;
}
