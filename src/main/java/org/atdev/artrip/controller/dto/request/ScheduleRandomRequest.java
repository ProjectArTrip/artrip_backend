package org.atdev.artrip.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.atdev.artrip.service.dto.command.ExhibitRandomCommand;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleRandomRequest extends BaseRandomRequest {

    @NotNull
    private LocalDate date;

    public ExhibitRandomCommand toCommand(Long userId){

        return ExhibitRandomCommand.builder()
                .userId(userId)
                .isDomestic(this.isDomestic)
                .region(this.region)
                .country(this.country)
                .date(date)
                .build();
    }
}
