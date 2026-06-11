package org.atdev.artrip.service.csv;

import com.opencsv.bean.CsvToBeanBuilder;
import org.atdev.artrip.global.apipayload.code.error.ExhibitErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.service.dto.command.AdminExhibitCreateCommand;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class AdminExhibitCsvParser {
    private AdminExhibitCsvParser() {
    }

    private static final String HEADER_FIRST_COLUMN = "제목";
    private static final Long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024;
    private static final int MAX_ROW_COUNT = 1000;

    public static List<AdminExhibitCreateCommand> parse(MultipartFile file, Long adminId) {
        if (file == null || file.isEmpty()) {
            throw new GeneralException(ExhibitErrorCode._CSV_EMPTY);
        }

        String name = file.getOriginalFilename();
        if (name == null || !name.toLowerCase().endsWith(".csv")) {
            throw new GeneralException(ExhibitErrorCode._CSV_INVALID_FORMAT);
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new GeneralException(ExhibitErrorCode._CSV_INVALID_FORMAT);
        }

        try (Reader reader = new StringReader(extractCsvBody(file))) {
            List<AdminExhibitCsvRow> rows = new CsvToBeanBuilder<AdminExhibitCsvRow>(reader)
                    .withType(AdminExhibitCsvRow.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withThrowExceptions(true)
                    .build().parse();

            if (rows.size() > MAX_ROW_COUNT) {
                throw new GeneralException(ExhibitErrorCode._CSV_INVALID_ROW);
            }

            return rows.stream()
                    .map(row -> toCommand(adminId, row)).toList();
        } catch (IOException e) {
            throw new GeneralException(ExhibitErrorCode._CSV_INVALID_FORMAT);
        } catch (RuntimeException e) {
            throw new GeneralException(ExhibitErrorCode._CSV_INVALID_ROW);
        }
    }

    private static String extractCsvBody(MultipartFile file) throws IOException {
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        if (!content.isEmpty() && content.charAt(0) == '\uFEFF') {
            content = content.substring(1);
        }

        String body = content.lines()
                .dropWhile(line -> !line.trim().startsWith(HEADER_FIRST_COLUMN))
                .filter(line -> !line.replace(",", "").isBlank())
                .collect(Collectors.joining("\n"));

        if (body.isBlank()) {
            throw new GeneralException(ExhibitErrorCode._CSV_INVALID_FORMAT);
        }

        return body;
    }

    private static AdminExhibitCreateCommand toCommand(Long adminId, AdminExhibitCsvRow row) {

        Set<String> genres = splitToSet(row.getGenres(), ",");
        Set<String> styles = splitToSet(row.getStyles(), ",");

        return AdminExhibitCreateCommand.of(
                adminId,
                row.getTitle(),
                row.getDescription(),
                row.getPosterUrl(),
                row.getTicketUrl(),
                row.getStartDate(),
                row.getEndDate(),
                row.getHallName(),
                row.getCountry(),
                row.getRegion(),
                row.getAddress(),
                row.getOpeningHours(),
                row.getPhone(),
                row.getLatitude(),
                row.getLongitude(),
                genres,
                styles
        );
    }

    private static Set<String> splitToSet(String raw, String regex) {
        if (raw == null || raw.isBlank()) return Set.of();
        return Arrays.stream(raw.split(regex))
                .map(String::trim)
                .map(s -> s.replaceAll("\\s+", ""))
                .filter(s -> !s.isBlank())
                .collect(Collectors.toUnmodifiableSet());
    }
}
