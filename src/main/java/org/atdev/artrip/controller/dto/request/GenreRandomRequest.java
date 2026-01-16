package org.atdev.artrip.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.atdev.artrip.service.dto.RandomQuery;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenreRandomRequest extends BaseRandomRequest {

    @NotNull
    private String singleGenre;

}
