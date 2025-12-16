package org.atdev.artrip.domain.home.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalizedRequestDto {

    @NotNull
    private Boolean isDomestic;

    private String region;
    private String country;
}
