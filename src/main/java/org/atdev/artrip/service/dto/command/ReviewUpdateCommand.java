package org.atdev.artrip.service.dto.command;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public record ReviewUpdateCommand(
        LocalDate date,
        String content,
        Long reviewId,
        Long userId,
        List<MultipartFile> images,
        List<Long> deleteImageIds
) {
}
