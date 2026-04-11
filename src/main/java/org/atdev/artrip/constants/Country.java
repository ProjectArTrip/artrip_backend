package org.atdev.artrip.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Country {

    ALL("전체"),
    KOREA("한국"),
    FRANCE("프랑스"),
    GERMANY("독일"),
    ITALY("이탈리아"),
    USA("미국"),
    AUSTRIA("오스트리아"),
    JAPAN("일본"),
    CHINA("중국");

    private final String label;
}
