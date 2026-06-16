package org.atdev.artrip.service.csv;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AdminExhibitCsvRow {

    @CsvBindByName(column = "제목", required = true)
    private String title;

    @CsvBindByName(column = "설명")
    private String description;

    @CsvBindByName(column = "전시 시작")
    private LocalDate startDate;

    @CsvBindByName(column = "전시 종료")
    private LocalDate endDate;

    @CsvBindByName(column = "국가", required = true)
    private String country;

    @CsvBindByName(column = "지역", required = true)
    private String region;

    @CsvBindByName(column = "장소(건물명)", required = true)
    private String hallName;

    @CsvBindByName(column = "장르")
    private String genres;

    @CsvBindByName(column = "전시 스타일")
    private String styles;

    @CsvBindByName(column = "예약링크")
    private String ticketUrl;

    @CsvBindByName(column = "이미지")
    private String posterUrl;

    @CsvBindByName(column = "위도")
    private BigDecimal latitude;

    @CsvBindByName(column = "경도")
    private BigDecimal longitude;

    @CsvBindByName(column = "전화번호")
    private String phone;

    @CsvBindByName(column = "영업시간")
    private String openingHours;

    @CsvBindByName(column = "주소")
    private String address;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getCountry() {
        return country;
    }

    public String getRegion() {
        return region;
    }

    public String getHallName() {
        return hallName;
    }

    public String getGenres() {
        return genres;
    }

    public String getStyles() {
        return styles;
    }

    public String getTicketUrl() {
        return ticketUrl;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public String getPhone() {
        return phone;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public String getAddress() {
        return address;
    }
}
