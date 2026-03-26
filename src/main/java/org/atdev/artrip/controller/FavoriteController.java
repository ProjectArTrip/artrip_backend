package org.atdev.artrip.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.response.FavoriteCursorResponse;
import org.atdev.artrip.controller.spec.FavoriteSpecification;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.FavoriteService;
import org.atdev.artrip.service.dto.condition.FavoriteSearchCondition;
import org.atdev.artrip.service.dto.result.FavoriteResult;
import org.atdev.artrip.utils.CursorPagination;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorites")
public class FavoriteController implements FavoriteSpecification {

    private final FavoriteService favoriteService;

    @Override
    @GetMapping
    public ResponseEntity<FavoriteCursorResponse> getFavorites(
            @Valid @ParameterObject FavoriteSearchCondition condition,
            @Valid @ParameterObject CursorPagination cursorPagination,
            @LoginUser Long userId
    ) {

        FavoriteResult result = favoriteService.getFavorites(userId, condition, cursorPagination);

        return ResponseEntity.ok(FavoriteCursorResponse.from(result));
    }

    @Override
    @PostMapping("/{exhibitId}")
    public ResponseEntity<Void> createFavorite(
            @LoginUser Long userId,
            @PathVariable Long exhibitId
    ) {

        favoriteService.create(userId, exhibitId);

        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping("/{exhibitId}")
    public ResponseEntity<Void> removeFavorite(
            @LoginUser Long userId,
            @PathVariable Long exhibitId
    ) {
        favoriteService.delete(userId, exhibitId);

        return ResponseEntity.noContent().build();
    }
}
