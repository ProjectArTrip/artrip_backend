package org.atdev.artrip.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.response.FavoriteListResponse;
import org.atdev.artrip.controller.spec.FavoriteSpecification;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.FavoriteService;
import org.atdev.artrip.service.dto.condition.FavoriteSearchCondition;
import org.atdev.artrip.service.dto.result.FavoriteResult;
import org.atdev.artrip.utils.CursorPagination;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("favorites")
public class FavoriteController implements FavoriteSpecification {

    private final FavoriteService favoriteService;

    @Override
    @GetMapping
    public ResponseEntity<FavoriteListResponse> getFavorites(
            @Valid @RequestBody FavoriteSearchCondition condition,
            @Valid CursorPagination cursorPagination,
            @LoginUser Long userId
    ) {

        FavoriteResult result = favoriteService.getFavorites(userId, condition, cursorPagination);

        return ResponseEntity.ok(FavoriteListResponse.from(result));
    }
}
