package org.atdev.artrip.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");


    public static String convertDate(LocalDate startDate, LocalDate endDate) {

        return String.format("%s ~ %s", startDate.format(formatter), endDate.format(formatter));
    }
}