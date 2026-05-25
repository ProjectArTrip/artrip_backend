package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.constants.KeywordType;
import org.atdev.artrip.constants.Role;
import org.atdev.artrip.constants.Status;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.domain.exhibitHall.ExhibitHall;
import org.atdev.artrip.domain.keyword.Keyword;
import org.atdev.artrip.global.apipayload.code.error.ExhibitErrorCode;
import org.atdev.artrip.global.apipayload.code.error.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.repository.*;
import org.atdev.artrip.service.csv.AdminExhibitCsvParser;
import org.atdev.artrip.service.dto.command.AdminExhibitCreateCommand;
import org.atdev.artrip.service.dto.command.AdminExhibitSearchCommand;
import org.atdev.artrip.service.dto.command.AdminExhibitUpdateCommand;
import org.atdev.artrip.service.dto.result.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminExhibitService {

    private final UserRepository userRepository;
    private final ExhibitRepository exhibitRepository;
    private final KeywordRepository keywordRepository;
    private final ExhibitHallRepository exhibitHallRepository;
    private final FavoriteRepository favoriteRepository;

    @Transactional(readOnly = true)
    public AdminExhibitListResult list(Long adminId, AdminExhibitSearchCommand command, Pageable pageable) {
        findAdminOrThrow(adminId);
        Page<AdminExhibitListItemResult> page = exhibitRepository.searchForAdmin(command, pageable);
        return AdminExhibitListResult.from(page);
    }

    @Transactional(readOnly = true)
    public AdminExhibitResult detail(Long adminId, Long exhibitId) {
        findAdminOrThrow(adminId);
        Exhibit exhibit = exhibitRepository.findByIdWithHall(exhibitId).orElseThrow(() -> new GeneralException(ExhibitErrorCode._EXHIBIT_NOT_FOUND));
        return AdminExhibitResult.from(exhibit);
    }

    @Transactional
    public AdminExhibitCreateResult create(AdminExhibitCreateCommand command) {
        findAdminOrThrow(command.adminId());
        Long exhibitId = createInternal(command);
        return AdminExhibitCreateResult.of(exhibitId);
    }

    @Transactional
    public AdminExhibitBulkCreateResult bulkCreateFromCsv(Long adminId, MultipartFile file) {
        findAdminOrThrow(adminId);

        List<AdminExhibitCreateCommand> commands = AdminExhibitCsvParser.parse(file, adminId);
        if (commands.isEmpty()) {
            throw new GeneralException(ExhibitErrorCode._CSV_EMPTY);
        }

        List<Long> savedIds = new ArrayList<>(commands.size());
        for (AdminExhibitCreateCommand command : commands) {
            savedIds.add(createInternal(command));
        }
        return AdminExhibitBulkCreateResult.of(savedIds);
    }

    @Transactional
    public void update(AdminExhibitUpdateCommand command) {
        findAdminOrThrow(command.adminId());

        if (command.startDate() != null && command.endDate() != null && command.startDate().isAfter(command.endDate())) {
            throw new GeneralException(ExhibitErrorCode._EXHIBIT_INVALID_DATE_RANGE);
        }

        Exhibit exhibit = exhibitRepository.findByIdWithHall(command.exhibitId()).orElseThrow(() -> new GeneralException(ExhibitErrorCode._EXHIBIT_NOT_FOUND));

        Set<Keyword> keywords = resolveAllKeywords(command.genres(), command.styles());

        exhibit.updateBasicInfo(
                command.title(),
                command.description(),
                command.posterUrl(),
                command.ticketUrl(),
                command.startDate(),
                command.endDate()
        );

        exhibit.changeStatus(command.status());
        exhibit.replaceKeywords(keywords);

        exhibit.getExhibitHall().updateInfo(
                command.exhibitHallName(),
                command.country(),
                command.region(),
                command.address(),
                command.openingHours(),
                command.phone(),
                command.latitude(),
                command.longitude(),
                command.isDomestic()
        );
    }

    @Transactional
    public void delete(Long adminId, Long exhibitId) {
        findAdminOrThrow(adminId);
        Exhibit exhibit = exhibitRepository.findById(exhibitId).orElseThrow(() -> new GeneralException(ExhibitErrorCode._EXHIBIT_NOT_FOUND));

        exhibit.replaceKeywords(Set.of());
        favoriteRepository.deleteByExhibitId(exhibitId);
        exhibitRepository.delete(exhibit);
    }

    private ExhibitHall upsertHall(String name, String country, String region, String address, String openingHours, String phone, BigDecimal latitude, BigDecimal longitude, boolean isDomestic) {
        return exhibitHallRepository.findByName(name)
                .map(existing -> {
                    existing.updateInfo(name, country, region, address, openingHours, phone, latitude, longitude, isDomestic);
                    return existing;
                })
                .orElseGet(() -> exhibitHallRepository.save(
                        ExhibitHall.create(name, country, region, address, openingHours, phone, latitude, longitude, isDomestic)
                ));
    }

    private Long createInternal(AdminExhibitCreateCommand command) {
        if (command.startDate() != null && command.endDate() != null && command.startDate().isAfter(command.endDate())) {
            throw new GeneralException(ExhibitErrorCode._EXHIBIT_INVALID_DATE_RANGE);
        }

        Set<Keyword> keywords = resolveAllKeywords(command.genres(), command.styles());

        ExhibitHall hall = upsertHall(
                command.exhibitHallName(),
                command.country(),
                command.region(),
                command.address(),
                command.openingHours(),
                command.phone(),
                command.latitude(),
                command.longitude(),
                command.isDomestic()
        );

        Status status = resolveStatus(command.startDate(), command.endDate());

        Exhibit exhibit = Exhibit.create(
                command.title(),
                command.description(),
                command.posterUrl(),
                command.ticketUrl(),
                command.startDate(),
                command.endDate(),
                status,
                hall
        );

        exhibit.replaceKeywords(keywords);
        return exhibitRepository.save(exhibit).getExhibitId();
    }

    private Status resolveStatus(LocalDate start, LocalDate end) {
        LocalDate today = LocalDate.now();
        if (end == null || start == null) return Status.UPCOMING;
        if (end.isBefore(today)) return Status.FINISHED;
        if (start.isAfter(today)) return Status.UPCOMING;
        if (!end.isAfter(today.plusDays(3))) return Status.ENDING_SOON;
        return Status.ONGOING;
    }

    private Set<Keyword> resolveAllKeywords(Set<String> genres, Set<String> styles) {
        Set<String> safeGenres = genres == null ? Set.of() : genres;
        Set<String> safeStyles = styles == null ? Set.of() : styles;
        if (safeGenres.isEmpty() && safeStyles.isEmpty()) return new HashSet<>();

        List<Keyword> found = keywordRepository.findGenresAndStyles(KeywordType.GENRE, safeGenres, KeywordType.STYLE, safeStyles);

        Set<String> foundGenreNames = found.stream()
                .filter(k -> k.getType() == KeywordType.GENRE)
                .map(Keyword::getName)
                .collect(Collectors.toSet());
        Set<String> foundStyleNames = found.stream()
                .filter(s -> s.getType() == KeywordType.STYLE)
                .map(Keyword::getName)
                .collect(Collectors.toSet());

        Set<String> missingGenres = new HashSet<>(safeGenres);
        missingGenres.removeAll(foundGenreNames);
        Set<String> missingStyles = new HashSet<>(safeStyles);
        missingStyles.removeAll(foundStyleNames);

        if (!missingGenres.isEmpty() || !missingStyles.isEmpty()) {
            log.warn("CSV 키워드 매칭 Error - genres: {}, styles: {}", missingGenres, missingStyles);
            throw new GeneralException(ExhibitErrorCode._EXHIBIT_KEYWORD_NOT_FOUND);
        }
        return new HashSet<>(found);
    }

    private User findAdminOrThrow(Long adminId) {
        User user = userRepository.findById(adminId).orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));
        if (user.getRole() != Role.ADMIN) {
            throw new GeneralException(UserErrorCode._USER_FORBIDDEN);
        }
        return user;
    }


}
