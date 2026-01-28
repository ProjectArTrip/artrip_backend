package org.atdev.artrip.service.dto.command;

import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.domain.review.Review;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReviewCommand(
        LocalDate date,
        String content,
        Long exhibitId,
        Long userId,
        java.util.List<MultipartFile> images) {


    public Review toEntity(User user, Exhibit exhibit) {

        return Review.builder()
                .user(user)
                .exhibit(exhibit)
                .content(this.content)
                .visitDate(this.date)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
