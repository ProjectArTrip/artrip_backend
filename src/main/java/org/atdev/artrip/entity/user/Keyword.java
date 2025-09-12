package org.atdev.artrip.entity.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "keyword", schema = "art_dev")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "keyword_id")
    private Long keywordId;

    @Column(name = "`group`", nullable = false) // group은 SQL 예약어라 `` 사용
    private String group;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "keyword")
    private List<UserKeyword> userKeywords;
}
