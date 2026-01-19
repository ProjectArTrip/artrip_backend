package org.atdev.artrip.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTimeUtils {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public static String convertDate(LocalDate startDate, LocalDate endDate) {

        if (startDate == null || endDate == null)
            return "";

        return String.format("%s ~ %s", startDate.format(formatter), endDate.format(formatter));
    }
}