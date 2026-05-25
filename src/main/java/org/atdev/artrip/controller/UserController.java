package org.atdev.artrip.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.request.DeviceTokenRequest;
import org.atdev.artrip.controller.dto.request.PushEnabledRequest;
import org.atdev.artrip.controller.dto.response.*;
import org.atdev.artrip.controller.spec.UserSpecification;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.UserHistoryService;
import org.atdev.artrip.service.UserService;
import org.atdev.artrip.controller.dto.request.NicknameRequest;
import org.atdev.artrip.controller.dto.response.MypageResponse;
import org.atdev.artrip.service.dto.result.ExhibitRecentResult;
import org.atdev.artrip.service.dto.result.MypageResult;
import org.atdev.artrip.service.dto.result.PushEnabledResult;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/me")
public class UserController implements UserSpecification {

    private final UserService userService;
    private final UserHistoryService userHistoryService;

    @Override
    @PatchMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MypageResponse> updateUserImage(
            @LoginUser Long userId,
            @RequestPart("image") MultipartFile image) {

        MypageResult result = userService.updateUserImage(userId, image);

        return ResponseEntity.ok(MypageResponse.from(result));
    }

    @Override
    @DeleteMapping("/image")
    public ResponseEntity<Void> deleteUserImage(
            @LoginUser Long userId) {

        userService.deleteUserImage(userId);

        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping
    public ResponseEntity<MypageResponse> updateNickname(
            @LoginUser Long userId,
            @RequestBody @Valid NicknameRequest request) {

        MypageResult result = userService.updateNickName(userId, request.nickName());

        return ResponseEntity.ok(MypageResponse.from(result));
    }

    @Override
    @GetMapping
    public ResponseEntity<MypageResponse> getMypage(
            @LoginUser Long userId) {

        MypageResult result = userService.getMypage(userId);

        return ResponseEntity.ok(MypageResponse.from(result));
    }

    @Override
    @GetMapping("/recent-exhibits")
    public ResponseEntity<ExhibitRecentResponse> getRecentExhibit(
            @LoginUser Long userId) {

        List<ExhibitRecentResult> results = userHistoryService.getRecentViews(userId);

        return ResponseEntity.ok(ExhibitRecentResponse.from(results));
    }

    @Override
    @PostMapping("/fcm-token")
    public ResponseEntity<Void> updateFcmToken(
            @LoginUser Long userId,
            @RequestBody @Valid DeviceTokenRequest request) {

        userService.updateFcmToken(userId, request.token());

        return ResponseEntity.noContent().build();

    }

    @Override
    @PatchMapping("/push-enabled")
    public ResponseEntity<Void> updatePushEnabled(
            @LoginUser Long userId,
            @RequestBody @Valid PushEnabledRequest request) {

        userService.updatePushEnabled(userId, request.enabled());

        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/push-enabled")
    public ResponseEntity<PushEnabledResponse> getPushEnabled(@LoginUser Long userId) {
        PushEnabledResult result = userService.getPushEnabled(userId);
        return ResponseEntity.ok(PushEnabledResponse.from(result));
    }
}