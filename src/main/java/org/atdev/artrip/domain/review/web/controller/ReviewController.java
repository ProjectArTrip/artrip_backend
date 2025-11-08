package org.atdev.artrip.domain.review.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.home.response.HomeListResponse;
import org.atdev.artrip.domain.review.service.ReviewService;
import org.atdev.artrip.global.apipayload.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 생성")
    @PostMapping("/")
    public ResponseEntity<ApiResponse<List<HomeListResponse>>> CreateReview(@RequestParam("images") List<MultipartFile> images,
                                                                            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                                            @RequestParam("content") String content,
                                                                            @AuthenticationPrincipal UserDetails userDetails){


        return ResponseEntity.ok(ApiResponse.onSuccess());
    }
}
