package org.atdev.artrip.domain.home.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenreRandomRequestDto extends BaseRandomRequestDto{

    @NotNull
    private String singleGenre;
}
