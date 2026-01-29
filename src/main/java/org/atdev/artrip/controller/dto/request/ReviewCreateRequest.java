package org.atdev.artrip.controller.dto.request;


import org.atdev.artrip.service.dto.command.ReviewCreateCommand;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;


public record ReviewCreateRequest (
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate date,
        String content
){

    public static ReviewCreateCommand toCommand(LocalDate date, String content, Long exhibitId, Long userId, List<MultipartFile> images){

        return new ReviewCreateCommand(date,content,exhibitId,userId,images);
    }

}
