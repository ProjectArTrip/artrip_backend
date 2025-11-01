package org.atdev.artrip.domain.favortie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalenderResponse {

    private int year;
    private int month;
    private List<LocalDate> exhibitDates;
}
