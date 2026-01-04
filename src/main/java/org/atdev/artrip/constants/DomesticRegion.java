package org.atdev.artrip.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DomesticRegion {

    SEOUL("서울", "https://arttrip.s3.ap-northeast-2.amazonaws.com/region/%EC%84%9C%EC%9A%B8.jpg"),
    GYEONGGI("경기", "https://arttrip.s3.ap-northeast-2.amazonaws.com/region/%EA%B2%BD%EA%B8%B0.jpg"),
    JEONLA("전라", "https://arttrip.s3.ap-northeast-2.amazonaws.com/region/%EC%A0%84%EB%9D%BC.jpg"),
    JEJU("제주", "https://arttrip.s3.ap-northeast-2.amazonaws.com/region/%EC%A0%9C%EC%A3%BC.jpg"),
    GYEONGSANG("경상", "https://arttrip.s3.ap-northeast-2.amazonaws.com/region/%EA%B2%BD%EC%83%81.jpg"),
    GANGWON("강원", "https://arttrip.s3.ap-northeast-2.amazonaws.com/region/%EA%B0%95%EC%9B%90.jpg"),
    CHUNGCHEONG("충청", "https://arttrip.s3.ap-northeast-2.amazonaws.com/region/%EC%B6%A9%EC%B2%AD.jpg");

    private final String region;
    private final String imageUrl;
}
