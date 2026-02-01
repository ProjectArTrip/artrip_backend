package org.atdev.artrip.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.spec.ReviewSpecification;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.ReviewService;
import org.atdev.artrip.controller.dto.request.ReviewCreateRequest;
import org.atdev.artrip.controller.dto.response.ExhibitReviewSliceResponse;
import org.atdev.artrip.controller.dto.request.ReviewUpdateRequest;
import org.atdev.artrip.controller.dto.response.ReviewSliceResponse;
import org.atdev.artrip.service.dto.command.ReviewCreateCommand;
import org.atdev.artrip.service.dto.command.ReviewUpdateCommand;
import org.atdev.artrip.service.dto.result.ExhibitReviewResult;
import org.atdev.artrip.service.dto.result.MyReviewResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController implements ReviewSpecification {

    private final ReviewService reviewService;

    @Override
    @PostMapping("/{exhibitId}")
    public ResponseEntity<Void> createReview(@PathVariable Long exhibitId,
                                             @RequestPart(value = "images",required = false) List<MultipartFile> images,
                                             @Valid @RequestPart(value = "request") ReviewCreateRequest request,
                                             @LoginUser Long userId){

        ReviewCreateCommand command = ReviewCreateRequest.toCommand(request.date(), request.content(),exhibitId, userId,images);
        reviewService.createReview(command);

        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping("/{reviewId}")
    public ResponseEntity<Void> updateReview(@PathVariable Long reviewId,
                                             @RequestPart(value = "images",required = false) List<MultipartFile> images,
                                             @RequestPart("request") ReviewUpdateRequest request,
                                             @LoginUser Long userId ){

        ReviewUpdateCommand command = ReviewUpdateRequest.toCommand(request.date(), request.content(),request.deleteImageIds(),reviewId, userId,images);
        reviewService.updateReview(command);

        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId,
                                             @LoginUser Long userId){

        reviewService.deleteReview(reviewId, userId);

        return ResponseEntity.noContent().build();
    }


    @Override
    @GetMapping("/all")
    public ResponseEntity<ReviewSliceResponse> getAllMyReview(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size,
            @LoginUser Long userId) {

        MyReviewResult result = reviewService.getAllReview(userId, cursor, size);
        ReviewSliceResponse response = ReviewSliceResponse.from(result);

        return ResponseEntity.ok(response);
    }


    @Override
    @GetMapping("/{exhibitId}")
    public ResponseEntity<ExhibitReviewSliceResponse> getExhibitReview(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable Long exhibitId) {

        ExhibitReviewResult result = reviewService.getExhibitReview(exhibitId, cursor, size);
        ExhibitReviewSliceResponse response = ExhibitReviewSliceResponse.from(result);


        return ResponseEntity.ok(response);
    }
}
