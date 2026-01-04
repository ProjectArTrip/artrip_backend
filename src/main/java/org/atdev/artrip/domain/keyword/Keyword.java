package org.atdev.artrip.domain.keyword;

import jakarta.persistence.*;
import lombok.*;
import org.atdev.artrip.constants.KeywordType;

import java.util.List;

@Entity
@Table(name = "keyword")
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KeywordType type;// 장르, 스타일

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "keyword")
    private List<UserKeyword> userKeywords;
}
