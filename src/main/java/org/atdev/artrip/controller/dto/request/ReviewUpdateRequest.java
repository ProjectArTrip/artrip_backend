package org.atdev.artrip.controller.dto.request;


import org.atdev.artrip.service.dto.command.ReviewCreateCommand;
import org.atdev.artrip.service.dto.command.ReviewUpdateCommand;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;


public record ReviewUpdateRequest (

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate date,
        String content,
        List<Long> deleteImageIds ){

    public static ReviewUpdateCommand toCommand(LocalDate date, String content, List<Long> deleteImageIds, Long reviewId, Long userId, List<MultipartFile> images){

        return new ReviewUpdateCommand(date,content,reviewId,userId,images,deleteImageIds);
    }

}