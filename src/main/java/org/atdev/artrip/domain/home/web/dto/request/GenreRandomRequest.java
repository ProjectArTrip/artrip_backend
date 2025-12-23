package org.atdev.artrip.domain.home.web.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenreRandomRequest extends BaseRandomRequest {

    @NotNull
    private String singleGenre;
}
