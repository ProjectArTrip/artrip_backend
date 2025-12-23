package org.atdev.artrip.domain.favortie.web.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryFavoriteResponse {

    private String country;
    private long favoriteCount;
    private boolean hasActive;
}
