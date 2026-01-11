package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.controller.dto.request.*;
import org.atdev.artrip.controller.dto.response.CursorPaginationResponse;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.repository.ExhibitHallRepository;
import org.atdev.artrip.repository.FavoriteExhibitRepository;
import org.atdev.artrip.converter.HomeConverter;
import org.atdev.artrip.controller.dto.response.FilterResponse;

import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.controller.dto.response.HomeListResponse;
import org.atdev.artrip.controller.dto.response.RegionResponse;
import org.atdev.artrip.domain.keyword.Keyword;
import org.atdev.artrip.domain.keyword.UserKeyword;
import org.atdev.artrip.repository.UserKeywordRepository;
import org.atdev.artrip.global.apipayload.code.status.UserError;
import org.atdev.artrip.global.apipayload.exception.GeneralException;

import org.atdev.artrip.global.s3.service.S3Service;
import org.atdev.artrip.controller.dto.request.ImageResizeRequest;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final ExhibitRepository exhibitRepository;
    private final UserKeywordRepository userkeywordRepository;
    private final ExhibitHallRepository exhibitHallRepository;
    private final UserRepository userRepository;
    private final HomeConverter homeConverter;
    private final S3Service s3Service;
    private final FavoriteExhibitRepository favoriteExhibitRepository;
    private final ModelMapper modelMapper;

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


    @Transactional(readOnly = true)
    public CursorPaginationResponse<HomeListResponse> searchExhibit(ExhibitFilterRequest request, ImageResizeRequest resizeRequest, Long userId) {

        Slice<Exhibit> slice = exhibitRepository.findExhibitByFilters(request, request.getSize(), request.getCursor());
        Set<Long> favoriteIds = getFavoriteIds(userId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        List<HomeListResponse> data = slice.getContent().stream()
                .map(exhibit -> {
                    String period = exhibit.getStartDate().format(formatter) + " - " + exhibit.getEndDate().format(formatter);

                    String resizedUrl = s3Service.buildResizeUrl(
                            exhibit.getPosterUrl(),
                            resizeRequest.w(),
                            resizeRequest.h(),
                            resizeRequest.f()
                    );

                    return HomeListResponse.builder()
                            .exhibit_id(exhibit.getExhibitId())
                            .title(exhibit.getTitle())
                            .posterUrl(resizedUrl)
                            .status(exhibit.getStatus())
                            .exhibitPeriod(period)
                            .isFavorite(favoriteIds.contains(exhibit.getExhibitId()))
                            .build();

                }).toList();

        Long nextCursor = (slice.hasNext() && !data.isEmpty()) ? slice.getContent().get(slice.getContent().size() - 1).getExhibitId() : null;

        return CursorPaginationResponse.of(data, slice.hasNext(), nextCursor);
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
                s3Service.buildResizeUrl(r.getPosterUrl(), resize.w(), resize.h(), resize.f())
        ));
      
        Set<Long> favoriteIds = getFavoriteIds(userId);
        setFavorites(results, favoriteIds);

        return results;
    }

    // 이번주 랜덤 전시 추천
    public List<HomeListResponse> getRandomSchedule(ScheduleRandomRequest request, Long userId, ImageResizeRequest resize){

        RandomExhibitRequest filter = homeConverter.from(request);
        List<HomeListResponse> results = exhibitRepository.findRandomExhibits(filter);

        results.forEach(r -> r.setPosterUrl(
                s3Service.buildResizeUrl(r.getPosterUrl(), resize.w(), resize.h(), resize.f())
        ));
      
        Set<Long> favoriteIds = getFavoriteIds(userId);
        setFavorites(results, favoriteIds);

        return results;
    }

    // 장르별 전시 랜덤 추천
    public List<HomeListResponse> getRandomGenre(GenreRandomRequest request, Long userId, ImageResizeRequest resize){

        RandomExhibitRequest filter = homeConverter.fromGenre(request);

        List<HomeListResponse> results = exhibitRepository.findRandomExhibits(filter);

        results.forEach(r -> r.setPosterUrl(
                s3Service.buildResizeUrl(r.getPosterUrl(), resize.w(), resize.h(), resize.f())
        ));
      
        Set<Long> favoriteIds = getFavoriteIds(userId);
        setFavorites(results, favoriteIds);

        return results;
    }

    // 오늘날 전시 랜덤 추천
    public List<HomeListResponse> getRandomToday(TodayRandomRequest request, Long userId, ImageResizeRequest resize){

        RandomExhibitRequest filter = homeConverter.fromToday(request);

        List<HomeListResponse> results = exhibitRepository.findRandomExhibits(filter);

        results.forEach(r -> r.setPosterUrl(
                s3Service.buildResizeUrl(r.getPosterUrl(), resize.w(), resize.h(), resize.f())
        ));
      
        Set<Long> favoriteIds = getFavoriteIds(userId);
        setFavorites(results, favoriteIds);


        return results;
    }


}