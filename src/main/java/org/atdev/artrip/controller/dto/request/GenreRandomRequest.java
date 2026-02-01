package org.atdev.artrip.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.atdev.artrip.service.dto.command.ExhibitRandomCommand;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenreRandomRequest extends BaseRandomRequest {

    @NotNull
    private String singleGenre;

    public ExhibitRandomCommand toCommand(Long userId){

        return ExhibitRandomCommand.builder()
                .userId(userId)
                .isDomestic(this.isDomestic)
                .region(this.region)
                .country(this.country)
                .singleGenre(singleGenre)
                .build();
    }

}
