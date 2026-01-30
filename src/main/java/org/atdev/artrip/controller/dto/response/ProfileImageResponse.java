package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.ProfileResult;

public record ProfileImageResponse (
        String imageUrl
) {

    public static ProfileImageResponse from(ProfileResult result){
        return new ProfileImageResponse(result.image());
    }
}
