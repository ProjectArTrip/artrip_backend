package org.atdev.artrip.domain.home.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.domain.auth.repository.UserRepository;
import org.atdev.artrip.domain.exhibit.data.Exhibit;
import org.atdev.artrip.domain.exhibit.web.dto.request.ExhibitFilterRequest;
import org.atdev.artrip.domain.exhibitHall.repository.ExhibitHallRepository;
import org.atdev.artrip.domain.favortie.repository.FavoriteExhibitRepository;
import org.atdev.artrip.domain.home.converter.HomeConverter;
import org.atdev.artrip.domain.home.response.FilterResponse;

import org.atdev.artrip.domain.exhibit.repository.ExhibitRepository;
import org.atdev.artrip.domain.home.response.HomeListResponse;
import org.atdev.artrip.domain.home.web.dto.request.*;
import org.atdev.artrip.domain.home.web.dto.response.RegionResponse;
import org.atdev.artrip.domain.keyword.data.Keyword;
import org.atdev.artrip.domain.keyword.data.UserKeyword;
import org.atdev.artrip.domain.keyword.repository.UserKeywordRepository;
import org.atdev.artrip.global.apipayload.code.status.UserError;
import org.atdev.artrip.global.apipayload.exception.GeneralException;

import org.atdev.artrip.global.s3.service.S3Service;
import org.atdev.artrip.global.s3.web.dto.request.ImageResizeRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeService {

    private final ExhibitRepository exhibitRepository;
    private final UserKeywordRepository userkeywordRepository;
    private final ExhibitHallRepository exhibitHallRepository;
    private final UserRepository userRepository;
    private final HomeConverter homeConverter;
    private final S3Service s3Service;
    private final FavoriteExhibitRepository favoriteExhibitRepository;

    private Set<Long> getFavoriteIds(Long userId) {
        if (userId == null) {
            return Collections.emptySet();
        }
        return favoriteExhibitRepository.findActiveExhibitIds(userId);
    }

//   //  큐레이션 전시
//    public List<HomeExhibitResponse> getCuratedExhibits() {
//        return exhibitRepository.findCuratedExhibits()
//                .stream()
//                .map(this::toHomeExhibitResponse)
//                .toList();
//    }

    private void setFavorites(List<HomeListResponse> result, Set<Long> favoriteIds) {
        result.forEach(r -> r.setFavorite(favoriteIds.contains(r.getExhibit_id())));
    }

    // 장르 전체 조회
    public List<String> getAllGenres() {
        return exhibitRepository.findAllGenres();
    }

    // 해외 국가 목록 조회
    public List<String> getOverseas(){
        return exhibitHallRepository.findAllOverseasCountries();
    }

    // 국내 지역 목록 조회
//    public List<String> getDomestic(){
//        return exhibitHallRepository.findAllDomesticRegions();
//    }

    public List<RegionResponse> getRegions() {
        return homeConverter.toResponseList();
    }


    //필터 전체 조회
    public FilterResponse getFilterExhibit(ExhibitFilterRequest dto, Long size, Long cursorId, Long userId) {

        Slice<Exhibit> slice = exhibitRepository.findExhibitByFilters(dto, size, cursorId);
        Set<Long> favoriteIds = getFavoriteIds(userId);
        return homeConverter.toFilterResponse(slice, favoriteIds);
    }

    // 사용자 맞춤 전시 랜덤 추천
    @Transactional
    public List<HomeListResponse> getRandomPersonalized(Long userId, PersonalizedRequest request, ImageResizeRequest resize){

        if (!userRepository.existsById(userId)) {
            throw new GeneralException(UserError._USER_NOT_FOUND);
        }

        List<Keyword> userKeywords = userkeywordRepository.findByUser_UserId(userId)
                .stream()
                .map(UserKeyword::getKeyword)
                .toList();

        RandomExhibitRequest filter = homeConverter.from(
                request,
                userKeywords
        );

        List<HomeListResponse> results = exhibitRepository.findRandomExhibits(filter);

        results.forEach(r -> r.setPosterUrl(
                s3Service.buildResizeUrl(r.getPosterUrl(), resize.getW(), resize.getH(), resize.getF())
        ));
      
        Set<Long> favoriteIds = getFavoriteIds(userId);
        setFavorites(results, favoriteIds);

        adjustLocationFields(
                results,
                request.getIsDomestic(),
                request.getRegion(),
                request.getCountry()
        );

        return results;
    }

    // 이번주 랜덤 전시 추천
    public List<HomeListResponse> getRandomSchedule(ScheduleRandomRequest request, Long userId, ImageResizeRequest resize){

        RandomExhibitRequest filter = homeConverter.from(request);
        List<HomeListResponse> results = exhibitRepository.findRandomExhibits(filter);

        results.forEach(r -> r.setPosterUrl(
                s3Service.buildResizeUrl(r.getPosterUrl(), resize.getW(), resize.getH(), resize.getF())
        ));
      
        Set<Long> favoriteIds = getFavoriteIds(userId);
        setFavorites(results, favoriteIds);

        adjustLocationFields(
                results,
                request.getIsDomestic(),
                request.getRegion(),
                request.getCountry()
        );

        return results;
    }

    // 장르별 전시 랜덤 추천
    public List<HomeListResponse> getRandomGenre(GenreRandomRequest request, Long userId, ImageResizeRequest resize){

        RandomExhibitRequest filter = homeConverter.fromGenre(request);

        List<HomeListResponse> results = exhibitRepository.findRandomExhibits(filter);

        results.forEach(r -> r.setPosterUrl(
                s3Service.buildResizeUrl(r.getPosterUrl(), resize.getW(), resize.getH(), resize.getF())
        ));
      
        Set<Long> favoriteIds = getFavoriteIds(userId);
        setFavorites(results, favoriteIds);

        adjustLocationFields(
                results,
                request.getIsDomestic(),
                request.getRegion(),
                request.getCountry()
        );

        return results;
    }

    // 오늘날 전시 랜덤 추천
    public List<HomeListResponse> getRandomToday(TodayRandomRequest request, Long userId, ImageResizeRequest resize){

        RandomExhibitRequest filter = homeConverter.fromToday(request);

        List<HomeListResponse> results = exhibitRepository.findRandomExhibits(filter);

        results.forEach(r -> r.setPosterUrl(
                s3Service.buildResizeUrl(r.getPosterUrl(), resize.getW(), resize.getH(), resize.getF())
        ));
      
        Set<Long> favoriteIds = getFavoriteIds(userId);
        setFavorites(results, favoriteIds);

        adjustLocationFields(
                results,
                request.getIsDomestic(),
                request.getRegion(),
                request.getCountry()
        );

        return results;
    }
    private void adjustLocationFields(List<HomeListResponse> results, boolean isDomestic, String region, String country) {

        boolean isWhole = ("전체".equals(region) || region == null) && ("전체".equals(country) || country == null);

        if (!isWhole) {
            results.forEach(r -> {
                r.setRegionName(null);
                r.setCountryName(null);
            });
            return;
        }

        if (isDomestic) {
            results.forEach(r -> r.setCountryName(null));
        } else {
            results.forEach(r -> r.setRegionName(null));
        }
    }

}