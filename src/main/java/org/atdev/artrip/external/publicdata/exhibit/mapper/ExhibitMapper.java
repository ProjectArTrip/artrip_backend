package org.atdev.artrip.external.publicdata.exhibit.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.Enum.Status;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.exhibitHall.data.ExhibitHall;
import org.atdev.artrip.external.publicdata.exhibit.dto.response.ExhibitItem;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExhibitMapper {

    private static final DateTimeFormatter DATE_FORMATTER_DOT = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final DateTimeFormatter DATE_FORMATTER_DASH = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Exhibit toExhibit(ExhibitItem item) {
        try {
            LocalDate startDate = parseEventPeriod(item.getEventPeriod(), true);
            LocalDate endDate = parseEventPeriod(item.getEventPeriod(), false);
            Status status = calculateStatus(startDate, endDate);

            return Exhibit.builder()
                    .title(truncate(item.getTitle(), 255))
                    .description(buildDescription(item))
                    .startDate(startDate)
                    .endDate(endDate)
                    .status(status)
                    .posterUrl(item.getThumbnail())
                    .ticketUrl(item.getUrl())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            log.warn("전시 정보 변환 실패 - title: {}, error: {}", item.getTitle(), e.getMessage());
            return null;
        }
    }

    public ExhibitHall toExhibitHall(ExhibitItem item) {
        if (item == null || !StringUtils.hasText(item.getPlace())) {
            return null;
        }

        return ExhibitHall.builder()
                .name(truncate(item.getPlace(), 255))
                .country("대한민국")
                .region(parseRegion(item.getArea()))
                .address(item.getPlaceAddr())
                .phone(item.getPhone())
                .isDomestic(true)
                .latitude(parseCoordinate(item.getGpsX()))
                .longitude(parseCoordinate(item.getGpsY()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private LocalDate parseEventPeriod(String eventPeriod, boolean isStart) {
        if (!StringUtils.hasText(eventPeriod)) {
            return null;
        }

        try {
            // "2020-12-17 ~ 2021-04-11" 형식
            if (eventPeriod.contains("~")) {
                String[] dates = eventPeriod.split("~");
                if (dates.length != 2) {
                    return null;
                }
                String dateStr = isStart ? dates[0].trim() : dates[1].trim();
                return LocalDate.parse(dateStr, DATE_FORMATTER_DASH);
            }

            return parseDate(eventPeriod);
        } catch (Exception e) {
            log.warn("eventPeriod 파싱 실패: {}", eventPeriod);
            return null;
        }
    }

    private LocalDate parseDate(String date) {
        if (!StringUtils.hasText(date)) {
            return null;
        }

        try {

            if (date.contains("-")) {
                return LocalDate.parse(date, DATE_FORMATTER_DASH);
            }

            if (date.contains(".")) {
                return LocalDate.parse(date, DATE_FORMATTER_DOT);
            }

            if (date.length() == 8) {
                return LocalDate.parse(date, DateTimeFormatter.BASIC_ISO_DATE);
            }

            return null;
        } catch (DateTimeParseException e) {
            log.warn("날짜 파싱 실패: {}", date);
            return null;
        }
    }

    private BigDecimal parseCoordinate(String coordinate) {
        if (!StringUtils.hasText(coordinate)) {
            return null;
        }

        try {
            return new BigDecimal(coordinate.trim());
        } catch (NumberFormatException e) {
            log.warn("좌표 파싱 실패: {}", coordinate);
            return null;
        }
    }

    private Status calculateStatus(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return Status.ONGOING;
        }

        LocalDate now = LocalDate.now();
        LocalDate threeDaysLater = now.plusDays(3);

        if (now.isBefore(startDate)) {
            return Status.UPCOMING;
        } else if (now.isAfter(endDate)) {
            return Status.FINISHED;
        } else if (endDate.isBefore(threeDaysLater)) {
            return Status.ENDING_SOON;
        } else {
            return Status.ONGOING;
        }
    }

    private String buildDescription(ExhibitItem item) {
        StringBuilder sb = new StringBuilder();

        if (StringUtils.hasText(item.getPerson())) {
            sb.append("작가: ").append(item.getPerson());
        }

        if (StringUtils.hasText(item.getVenue())) {
            if (sb.length() > 0) sb.append("\n");
            sb.append("장소: ").append(item.getVenue());
        }

        if (StringUtils.hasText(item.getSubDescription())) {
            if (sb.length() > 0) sb.append("\n\n");
            sb.append(item.getSubDescription());
        }

        if (StringUtils.hasText(item.getCharge())) {
            if (sb.length() > 0) sb.append("\n\n");
            sb.append("관람료: ").append(item.getCharge());
        }

        return truncate(sb.toString(), 2000);
    }

    private String parseRegion(String area) {
        if (!StringUtils.hasText(area)) return null;
        return area.contains(" ") ? area.split(" ")[0] : area;
    }

    private String truncate(String str, int length) {
        if (!StringUtils.hasText(str)) return null;
        return str.length() > length ? str.substring(0, length) : str;
    }
}
