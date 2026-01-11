package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.domain.favorite.FavoriteExhibit;
import org.atdev.artrip.controller.dto.response.CalenderResponse;
import org.atdev.artrip.controller.dto.response.FavoriteResponse;
import org.atdev.artrip.repository.FavoriteExhibitRepository;
import org.atdev.artrip.global.apipayload.code.status.ExhibitErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteExhibitService {

    private final FavoriteExhibitRepository favoriteExhibitRepository;
    private final UserRepository userRepository;
    private final ExhibitRepository exhibitRepository;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    @Transactional
    public FavoriteResponse addFavorite(Long userId, Long exhibitId) {
        log.info("Adding favorite exhibit. userId: {}, exhibitId: {}", userId, exhibitId);

        User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));

        Exhibit exhibit = exhibitRepository.findById(exhibitId).orElseThrow(() -> new GeneralException(ExhibitErrorCode._EXHIBIT_NOT_FOUND));

        Optional<FavoriteExhibit> existing = favoriteExhibitRepository.findByUserAndExhibit(userId, exhibitId);

        FavoriteExhibit favorite;

        if (existing.isPresent()) {
            favorite = existing.get();
            if (favorite.isStatus()) {
                log.warn("Exhibit Already in favorites: userId: {}, exhibitId: {}", userId, exhibitId);
                return toFavoriteResponse(favorite);
            }
            favorite.setStatus(true);
            exhibit.increaseFavoriteCount();
            log.info("Reactivating favorite exhibit: {}", favorite.getFavoriteId());
        } else {
            favorite = FavoriteExhibit.builder()
                    .user(user)
                    .exhibit(exhibit)
                    .status(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            exhibit.increaseFavoriteCount();

            log.info("Creating new favorite");
        }

        FavoriteExhibit saved = favoriteExhibitRepository.save(favorite);
        log.info("Favorite exhibit added successfully. favoriteId: {}", saved.getFavoriteId());

        return toFavoriteResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<FavoriteResponse> getAllFavorites(Long userId) {
        log.info("Getting favorites for exhibit. userId: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new GeneralException(UserErrorCode._USER_NOT_FOUND);
        }

        List<FavoriteExhibit> favorites = favoriteExhibitRepository.findAllActive(userId);
        log.info("Favorites found: {}", favorites);

        return favorites.stream()
                .map(this::toFavoriteResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FavoriteResponse> getFavoritesByDate(Long userId, LocalDate date) {
        log.info("Getting favorites for exhibit by date. userID: {}, date: {}", userId, date);

        if (!userRepository.existsById(userId)) {
            throw new GeneralException(UserErrorCode._USER_NOT_FOUND);
        }

        List<FavoriteExhibit> favorites = favoriteExhibitRepository.findActiveByDate(userId, date);
        log.info("Favorites found: {}", favorites.size());

        return favorites.stream()
                .map(this::toFavoriteResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FavoriteResponse> getFavoritesByCountry(Long userId, String country) {
        log.info("Getting favorites for exhibit by country. userId: {}, country: {}", userId, country);

        if (!userRepository.existsById(userId)) {
            throw new GeneralException(UserErrorCode._USER_NOT_FOUND);
        }

        List<FavoriteExhibit> favorites = favoriteExhibitRepository.findActiveByCountry(userId, country);
        log.info("Favorites found: {}", favorites.size());

        return favorites.stream()
                .map(this::toFavoriteResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CalenderResponse getCalenderDates(Long userId, int year, int month) {
        log.info("Getting calendar dates for userId: {}, year: {}, month: {}", userId, year, month);

        if (!userRepository.existsById(userId)) {
            throw new GeneralException(UserErrorCode._USER_NOT_FOUND);
        }

        List<String> dateStrings = favoriteExhibitRepository.findDatesByYearMonth(userId, year, month);

        List<LocalDate> exhibitDates = dateStrings.stream()
                        .map(LocalDate::parse)
                        .toList();
        log.info("Exhibit dates found: {}", exhibitDates.size());

        return CalenderResponse.builder()
                .year(year)
                .month(month)
                .exhibitDates(exhibitDates)
                .build();
    }

    @Transactional(readOnly = true)
    public List<String> getFavoriteCountries(Long userId) {
        log.info("Getting favorite countries for exhibit. userId: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new GeneralException(UserErrorCode._USER_NOT_FOUND);
        }
        List<String> countries = favoriteExhibitRepository.findDistinctCountries(userId);
        log.info("Favorite countries found: {}", countries);

        return countries;
    }

    @Transactional(readOnly = true)
    public boolean isFavorite(Long userId, Long exhibitId) {
        return favoriteExhibitRepository.existsActive(userId, exhibitId);
    }

    @Transactional
    public void removeFavorite(Long userId, Long exhibitId) {
        log.info("Removing favorite exhibit. userId: {}, exhibitId: {}", userId, exhibitId);

        FavoriteExhibit favorite = favoriteExhibitRepository.findActive(userId, exhibitId)
                .orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));

        favorite.setStatus(false);
        favorite.getExhibit().decreaseFavoriteCount();

        favoriteExhibitRepository.save(favorite);
        log.info("Favorite exhibit removed successfully. favoriteId: {}", favorite.getFavoriteId());
    }

    private FavoriteResponse toFavoriteResponse(FavoriteExhibit favorite) {
        Exhibit exhibit = favorite.getExhibit();
        var hall = exhibit.getExhibitHall();

        String period = exhibit.getStartDate().format(fmt) + " - " + exhibit.getEndDate().format(fmt);

        return FavoriteResponse.builder()
                .favoriteId(favorite.getFavoriteId())
                .exhibitId(exhibit.getExhibitId())
                .title(exhibit.getTitle())
                .posterUrl(exhibit.getPosterUrl())
                .exhibitStatus(exhibit.getStatus())
                .active(favorite.isStatus())
                .exhibitPeriod(period)
                .exhibitHallName(hall != null ? hall.getName() : null )
                .country(hall != null ? hall.getCountry() : null)
                .region(hall != null ? hall.getRegion() : null)
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}
