package org.atdev.artrip.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTimeUtils {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final Pattern TIME_RANGE_PATTERN = Pattern.compile("(\\d{1,2})(시|:)(\\d{0,2})\\s*~\\s*(\\d{1,2})(시|:)(\\d{0,2})");

    public static String convertDate(LocalDate startDate, LocalDate endDate) {

        if (startDate == null && endDate == null) return "";
        if (endDate == null) return startDate.format(formatter);
        return String.format("%s - %s", startDate.format(formatter), endDate.format(formatter));
    }

    public static String normalizeOpeningHours(String raw) {
        if (raw == null || raw.isBlank()) return "";

        Matcher matcher = TIME_RANGE_PATTERN.matcher(raw);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String startHour = String.format("%02d", Integer.parseInt(matcher.group(1)));
            String startMin = matcher.group(3).isEmpty() ? "00" : String.format("%02d", Integer.parseInt(matcher.group(3)));
            String endHour = String.format("%02d", Integer.parseInt(matcher.group(4)));
            String endMin = matcher.group(6).isEmpty() ? "00" : String.format("%02d", Integer.parseInt(matcher.group(6)));

            matcher.appendReplacement(sb, startHour + ":" + startMin + " - " + endHour + ":" + endMin);
        }
        matcher.appendTail(sb);

        return sb.toString();
    }
}