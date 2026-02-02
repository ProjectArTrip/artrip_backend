package org.atdev.artrip.controller;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.request.FavoriteRequest;
import org.atdev.artrip.controller.dto.response.FavoriteListResponse;
import org.atdev.artrip.controller.spec.FavoriteSpecification;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.FavoriteService;
import org.atdev.artrip.service.dto.command.FavoriteCondition;
import org.atdev.artrip.service.dto.result.FavoriteResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("favorites")
public class FavoriteController implements FavoriteSpecification {

    private final FavoriteService favoriteService;

    @Override
    @GetMapping()
    public ResponseEntity<FavoriteListResponse> getFavorites(
            @ModelAttribute FavoriteRequest request,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") Long size,
            @LoginUser Long userId) {

        FavoriteCondition condition = request.toCommand(userId, cursor, size);
        FavoriteResult result = favoriteService.getFavorites(condition);

        return ResponseEntity.ok(FavoriteListResponse.from(result));

    }

}
