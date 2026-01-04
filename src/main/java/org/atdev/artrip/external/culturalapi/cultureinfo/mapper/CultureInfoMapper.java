package org.atdev.artrip.external.culturalapi.cultureinfo.mapper;

import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.constants.Status;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.domain.exhibitHall.ExhibitHall;
import org.atdev.artrip.external.culturalapi.cultureinfo.web.dto.response.CultureInfoDetailItem;
import org.atdev.artrip.external.culturalapi.cultureinfo.web.dto.response.CultureInfoItem;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
@Component
public class CultureInfoMapper {

    private static final DateTimeFormatter DATE_FORMATTER_DOT = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final DateTimeFormatter DATE_FORMATTER_DASH = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Exhibit toExhibit(CultureInfoItem item) {
        if  (item == null) return null;

        try {
            LocalDate startDate = parseDate(item.getStartDate());
            LocalDate endDate = parseDate(item.getEndDate());

            if (endDate != null && endDate.isBefore(LocalDate.now())) {
                return null;
            }

            Status status = calculateStatus(startDate, endDate);
            if (status == Status.FINISHED) {
                return null;
            }

            return Exhibit.builder()
                    .title(truncate(item.getTitle(), 255))
                    .description(buildDescription(item))
                    .startDate(startDate)
                    .endDate(endDate)
                    .status(status)
                    .posterUrl(item.getThumbnail())
                    .ticketUrl(null)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            log.warn("전시 정보 변환 실패 - title: {}, error: {}", item.getTitle(), e.getMessage());
            return null;
        }
    }

    public void mergeDetailToExhibit(Exhibit exhibit, CultureInfoDetailItem detail) {
        if (exhibit == null || detail == null ) return;

        if (StringUtils.hasText(detail.getUrl())) {
            exhibit.setTicketUrl(truncate(detail.getUrl(), 500));
        }

        if (StringUtils.hasText(detail.getContents1())) {
            String existingDesc = exhibit.getDescription();
            String enhancedDesc = existingDesc +"\n\n" + detail.getContents1();
            exhibit.setDescription(truncate(enhancedDesc, 2000));
        }

        exhibit.setUpdatedAt(LocalDateTime.now());
    }

    public ExhibitHall toExhibitHall(CultureInfoItem item) {
        if (item == null || !StringUtils.hasText(item.getPlace())) {
            return null;
        }

        return ExhibitHall.builder()
                .name(truncate(item.getPlace(), 255))
                .country("대한민국")
                .region(parseRegion(item.getArea()))
                .address(item.getFullAddress())
                .phone(null)
                .homepageUrl(null)
                .isDomestic(true)
                .latitude(parseCoordinate(item.getGpsX()))
                .longitude(parseCoordinate(item.getGpsY()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void mergeDetailToHall(ExhibitHall hall, CultureInfoDetailItem detail) {
        if (hall == null || detail == null) return;

        boolean updated = false;

        if (StringUtils.hasText(detail.getPhone()) && !StringUtils.hasText(hall.getPhone())) {
            String cleanPhone = detail.getPhone().replaceAll("[^0-9-]", "");

            if (StringUtils.hasText(cleanPhone)) {
                hall.setPhone(truncate(cleanPhone, 20));
                updated = true;
            }
        }

        if (StringUtils.hasText(detail.getPlaceUrl()) && !StringUtils.hasText(hall.getHomepageUrl())) {
            hall.setHomepageUrl(truncate(detail.getPlaceUrl(), 500));
            updated = true;
        }

        if (StringUtils.hasText(detail.getPlaceAddr()) && (!StringUtils.hasText(hall.getAddress()) || hall.getAddress().length() < detail.getPlaceAddr().length())) {
            hall.setAddress(truncate(detail.getPlaceAddr(), 500));
        }

        if (updated) {
            hall.setUpdatedAt(LocalDateTime.now());
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
            log.warn("날짜 파싱 실패 : {}", date);
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

    private String buildDescription(CultureInfoItem item) {
        StringBuilder sb = new StringBuilder();

        if (StringUtils.hasText(item.getRealmName())) {
            sb.append("분류: ").append(item.getRealmName());
        }

        if (StringUtils.hasText(item.getArea())) {
            if (sb.length() > 0) {
                sb.append("지역 :").append(item.getArea());
            }
        }

        if (StringUtils.hasText(item.getPlace())) {
            if (sb.length() > 0) sb.append("\n");
            sb.append("장소: ").append(item.getPlace());
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
