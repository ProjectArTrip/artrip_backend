package org.atdev.artrip.domain.search;

import jakarta.persistence.*;
import lombok.*;
import org.atdev.artrip.domain.auth.User;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;


@Entity
@Table(name = "search_history")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "search_history_id")
    private Long searchHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDate createdAt;

    @Column(name = "content", nullable = false)
    private String content;

    public static SearchHistory of(Long searchHistoryId, User user, String content, LocalDate createdAt){
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.searchHistoryId = searchHistoryId;
        searchHistory.user = user;
        searchHistory.content = content;
        searchHistory.createdAt = createdAt;
        return searchHistory;
    }
}
