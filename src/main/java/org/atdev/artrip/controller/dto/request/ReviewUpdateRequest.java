package org.atdev.artrip.controller.dto.request;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.atdev.artrip.service.dto.command.ReviewUpdateCommand;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;


public record ReviewUpdateRequest (

        @NotNull(message = "방문일을 선택해주세요.")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate date,

        @NotNull(message = "리뷰 내용을 입력해주세요.")
        @Size(min = 20, max = 500, message = "리뷰는 20자 이상 500자 이하로 작성해주세요.")
        String content,

        List<Long> deleteImageIds ){

    public static ReviewUpdateCommand toCommand(LocalDate date, String content, List<Long> deleteImageIds, Long reviewId, Long userId, List<MultipartFile> images){

        return new ReviewUpdateCommand(date,content,reviewId,userId,images,deleteImageIds);
    }

}