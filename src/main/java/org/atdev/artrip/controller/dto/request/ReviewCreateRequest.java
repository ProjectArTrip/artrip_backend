package org.atdev.artrip.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.atdev.artrip.service.dto.command.ReviewCommand;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;


public record ReviewCreateRequest (
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate date,
        String content
){

    public static ReviewCommand toCommand(LocalDate date, String content, Long exhibitId, Long userId, List<MultipartFile> images){

        return new ReviewCommand(date,content,exhibitId,userId,images);
    }
}
