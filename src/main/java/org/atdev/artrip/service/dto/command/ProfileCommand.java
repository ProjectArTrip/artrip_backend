package org.atdev.artrip.service.dto.command;

import org.springframework.web.multipart.MultipartFile;

public record ProfileCommand(
        Long userId,
        MultipartFile image
) {

    public static ProfileCommand of(Long userId, MultipartFile image) {
        return new ProfileCommand(userId, image);
    }

    public static ProfileCommand of(Long userId) {
        return new ProfileCommand(userId,null);
    }

}
