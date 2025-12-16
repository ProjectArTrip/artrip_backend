package org.atdev.artrip.domain.home.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleRandomRequestDto {

    @NotNull
    private Boolean isDomestic;

    private String region;
    private String country;

    @NotNull
    private LocalDate date;
}
