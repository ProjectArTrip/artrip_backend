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
import org.atdev.artrip.service.dto.result.AdminExhibitBulkCreateResult;
import org.atdev.artrip.service.dto.result.AdminExhibitCreateResult;
import org.atdev.artrip.service.dto.result.AdminExhibitListItemResult;
import org.atdev.artrip.service.dto.result.AdminExhibitResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public Page<AdminExhibitListItemResult> list(Long adminId, AdminExhibitSearchCommand command, Pageable pageable) {
        findAdminOrThrow(adminId);
        return exhibitRepository.searchForAdmin(command, pageable);
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
        Long exhibitId = createInternal(command, null, null);
        return AdminExhibitCreateResult.of(exhibitId);
    }

    @Transactional
    public AdminExhibitBulkCreateResult bulkCreateFromCsv(Long adminId, MultipartFile file) {
        findAdminOrThrow(adminId);

        List<AdminExhibitCreateCommand> commands = AdminExhibitCsvParser.parse(file, adminId);
        if (commands.isEmpty()) {
            throw new GeneralException(ExhibitErrorCode._CSV_EMPTY);
        }

        Set<String> allGenres = commands.stream().flatMap(c -> c.genres().stream()).collect(Collectors.toSet());
        Set<String> allStyles = commands.stream().flatMap(c -> c.styles().stream()).collect(Collectors.toSet());
        Set<String> allHallNames = commands.stream()
                .map(AdminExhibitCreateCommand::exhibitHallName)
                .filter(name -> name != null && !name.isBlank())
                .collect(Collectors.toSet());

        List<Keyword> foundKeywords = keywordRepository.findGenresAndStyles(KeywordType.GENRE, allGenres, KeywordType.STYLE, allStyles);

        Set<String> foundGenreNames = foundKeywords.stream()
                .filter(k -> k.getType() == KeywordType.GENRE)
                .map(Keyword::getName).collect(Collectors.toSet());
        Set<String> foundStyleNames = foundKeywords.stream()
                .filter(k -> k.getType() == KeywordType.STYLE)
                .map(Keyword::getName).collect(Collectors.toSet());

        validateMissingKeywords(allGenres, allStyles, foundGenreNames, foundStyleNames);

        Map<String, Keyword> keywordByName = foundKeywords.stream()
                .collect(Collectors.toMap(Keyword::getName, k -> k));
        Map<String, ExhibitHall> hallByName = exhibitHallRepository.findByNameIn(allHallNames).stream()
                .collect(Collectors.toMap(ExhibitHall::getName, h -> h));

        List<Long> savedIds = new ArrayList<>(commands.size());
        for (AdminExhibitCreateCommand command : commands) {
            savedIds.add(createInternal(command, keywordByName, hallByName));
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

    private ExhibitHall upsertHall(AdminExhibitCreateCommand command) {
        return exhibitHallRepository.findByName(command.exhibitHallName())
                .map(existing -> {
                    applyHallInfo(existing, command);
                    return existing;
                })
                .orElseGet(() -> exhibitHallRepository.save(buildHall(command)));
    }

    private ExhibitHall buildHall(AdminExhibitCreateCommand command) {
        return ExhibitHall.create(
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

    private Long createInternal(AdminExhibitCreateCommand command, Map<String, Keyword> keywordCache, Map<String, ExhibitHall> hallCache) {

        if (command.startDate() != null && command.endDate() != null && command.startDate().isAfter(command.endDate())) {
            throw new GeneralException(ExhibitErrorCode._EXHIBIT_INVALID_DATE_RANGE);
        }

        Set<Keyword> keywords = resolveKeywords(command, keywordCache);
        ExhibitHall hall = resolveHall(command, hallCache);
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

    private Set<Keyword> resolveKeywords(AdminExhibitCreateCommand command, Map<String, Keyword> cache) {
        if (cache != null) {
            return Stream.concat(
                            command.genres().stream(),command.styles().stream())
                    .map(name -> {
                        Keyword k = cache.get(name);
                        if (k == null) {
                            log.warn("CSV 키워드 cache miss - key: {}", name);
                            throw new GeneralException(ExhibitErrorCode._EXHIBIT_KEYWORD_NOT_FOUND);
                        }
                        return k;
                    })
                    .collect(Collectors.toCollection(HashSet::new));
        }
        return resolveAllKeywords(command.genres(), command.styles());
    }

    private ExhibitHall resolveHall(AdminExhibitCreateCommand command, Map<String, ExhibitHall> cache) {

        if (cache != null) {
            ExhibitHall hall = cache.get(command.exhibitHallName());
            if (hall == null) {
                hall = exhibitHallRepository.save(buildHall(command));
                cache.put(command.exhibitHallName(), hall);
                return hall;
            }
            applyHallInfo(hall, command);

            return hall;
        }
        return upsertHall(command);
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

        validateMissingKeywords(safeGenres, safeStyles, foundGenreNames, foundStyleNames);
        return new HashSet<>(found);
    }

    private void validateMissingKeywords(Set<String> requestedGenres, Set<String> requestedStyles, Set<String> foundGenreNames, Set<String> foundStyleNames) {
        Set<String> missingGenres = new HashSet<>(requestedGenres);
        missingGenres.removeAll(foundGenreNames);
        Set<String> missingStyles = new HashSet<>(requestedStyles);
        missingStyles.removeAll(foundStyleNames);

        if (!missingGenres.isEmpty() || !missingStyles.isEmpty()) {
            log.warn("CSV 키워드 매칭 에러 - genres: {}, styles: {}", missingGenres, missingStyles);
            throw new GeneralException(ExhibitErrorCode._EXHIBIT_KEYWORD_NOT_FOUND);
        }
    }

    private void applyHallInfo(ExhibitHall hall, AdminExhibitCreateCommand command) {
        hall.updateInfo(
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

    private User findAdminOrThrow(Long adminId) {
        User user = userRepository.findById(adminId).orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));
        if (user.getRole() != Role.ADMIN) {
            throw new GeneralException(UserErrorCode._USER_FORBIDDEN);
        }
        return user;
    }


}
