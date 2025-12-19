package org.atdev.artrip.domain.home.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class TodayRandomRequestDto extends BaseRandomRequestDto{

}

