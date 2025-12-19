package org.atdev.artrip.domain.favortie.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.auth.data.User;
import org.atdev.artrip.domain.auth.repository.UserRepository;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.exhibit.repository.ExhibitRepository;
import org.atdev.artrip.domain.favortie.data.FavoriteExhibit;
import org.atdev.artrip.domain.favortie.web.dto.response.CalenderResponse;
import org.atdev.artrip.domain.favortie.web.dto.response.FavoriteResponse;
import org.atdev.artrip.domain.favortie.repository.FavoriteExhibitRepository;
import org.atdev.artrip.global.apipayload.code.status.CommonError;
import org.atdev.artrip.global.apipayload.code.status.ExhibitError;
import org.atdev.artrip.global.apipayload.code.status.UserError;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.global.apipayload.exception.handler.ErrorHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

        User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(UserError._USER_NOT_FOUND));

        Exhibit exhibit = exhibitRepository.findById(exhibitId).orElseThrow(() -> new GeneralException(ExhibitError._EXHIBIT_NOT_FOUND));

        if (favoriteExhibitRepository.existsByUser_UserIdAndExhibit_ExhibitId(userId, exhibitId)) {
            log.info("Exhibit with id {} already exists", exhibitId);
            throw new ErrorHandler(CommonError._BAD_REQUEST);
        }

        FavoriteExhibit favorite = FavoriteExhibit.builder()
                .user(user)
                .exhibit(exhibit)
                .createdAt(LocalDateTime.now())
                .build();
        FavoriteExhibit saved = favoriteExhibitRepository.save(favorite);
        log.info("Favorite exhibit added successfully. favoriteId: {}", saved.getFavoriteId());

        return toFavoriteResponse(saved);
    }

    public List<FavoriteResponse> getAllFavorites(Long userId) {
        log.info("Getting favorites for exhibit. userId: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new GeneralException(UserError._USER_NOT_FOUND);
        }

        List<FavoriteExhibit> favorites = favoriteExhibitRepository.findAllByUserIdWithExhibit(userId);
        log.info("Favorites found: {}", favorites);

        return favorites.stream()
                .map(this::toFavoriteResponse)
                .collect(Collectors.toList());
    }

    public List<FavoriteResponse> getFavoritesByDate(Long userId, LocalDate date) {
        log.info("Getting favorites for exhibit by date. userID: {}, date: {}", userId, date);

        if (!userRepository.existsById(userId)) {
            throw new GeneralException(UserError._USER_NOT_FOUND);
        }

        List<FavoriteExhibit> favorites = favoriteExhibitRepository.findByUserIdAndDate(userId, date);
        log.info("Favorites found: {}", favorites.size());

        return favorites.stream()
                .map(this::toFavoriteResponse)
                .collect(Collectors.toList());
    }

    public List<FavoriteResponse> getFavoritesByCountry(Long userId, String country) {
        log.info("Getting favorites for exhibit by country. userId: {}, country: {}", userId, country);

        if (!userRepository.existsById(userId)) {
            throw new GeneralException(UserError._USER_NOT_FOUND);
        }

        List<FavoriteExhibit> favorites = favoriteExhibitRepository.findByUserIdAndCountry(userId, country);
        log.info("Favorites found: {}", favorites.size());

        return favorites.stream()
                .map(this::toFavoriteResponse)
                .collect(Collectors.toList());
    }

    public CalenderResponse getCalenderDates(Long userId, int year, int month) {
        log.info("Getting calendar dates for userId: {}, year: {}, month: {}", userId, year, month);

        if (!userRepository.existsById(userId)) {
            throw new GeneralException(UserError._USER_NOT_FOUND);
        }

        List<String> dateStrings = favoriteExhibitRepository.findExhibitDatesByUserIdAndYearMonth(userId, year, month);

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

    public List<String> getFavoriteCountries(Long userId) {
        log.info("Getting favorite countries for exhibit. userId: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new GeneralException(UserError._USER_NOT_FOUND);
        }
        List<String> countries = favoriteExhibitRepository.findDistinctCountriesByUserId(userId);
        log.info("Favorite countries found: {}", countries);

        return countries;
    }

    public boolean isFavorite(Long userId, Long exhibitId) {
        return favoriteExhibitRepository.existsByUser_UserIdAndExhibit_ExhibitId(userId, exhibitId);
    }

    public void removeFavorite(Long userId, Long exhibitId) {
        log.info("Removing favorite exhibit. userId: {}, exhibitId: {}", userId, exhibitId);

        FavoriteExhibit favorite = favoriteExhibitRepository.findByUserIdAndExhibitId(userId, exhibitId)
                .orElseThrow(() -> new GeneralException(UserError._USER_NOT_FOUND));

        favoriteExhibitRepository.delete(favorite);
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
                .status(exhibit.getStatus())
                .exhibitPeriod(period)
                .exhibitHallName(hall != null ? hall.getName() : null )
                .country(hall != null ? hall.getCountry() : null)
                .region(hall != null ? hall.getRegion() : null)
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}
