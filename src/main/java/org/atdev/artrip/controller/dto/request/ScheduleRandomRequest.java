package org.atdev.artrip.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleRandomRequest extends BaseRandomRequest {

    @NotNull
    private LocalDate date;

}
