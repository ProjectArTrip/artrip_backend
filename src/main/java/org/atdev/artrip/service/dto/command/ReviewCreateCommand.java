package org.atdev.artrip.service.dto.command;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public record ReviewCreateCommand(
        LocalDate date,
        String content,
        Long exhibitId,
        Long userId,
        List<MultipartFile> images
        ) {

}
