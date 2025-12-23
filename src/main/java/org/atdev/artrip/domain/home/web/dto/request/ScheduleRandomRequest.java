package org.atdev.artrip.domain.home.web.dto.request;

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
