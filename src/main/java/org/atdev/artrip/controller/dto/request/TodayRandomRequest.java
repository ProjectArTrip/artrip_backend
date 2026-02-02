package org.atdev.artrip.controller.dto.request;

import lombok.*;
import org.atdev.artrip.service.dto.command.ExhibitRandomCommand;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class TodayRandomRequest extends BaseRandomRequest {

    public ExhibitRandomCommand toCommand(Long userId){

        return ExhibitRandomCommand.builder()
                .userId(userId)
                .isDomestic(this.isDomestic)
                .region(this.region)
                .country(this.country)
                .build();
    }
}

