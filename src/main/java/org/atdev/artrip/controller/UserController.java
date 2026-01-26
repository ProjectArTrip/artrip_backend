package org.atdev.artrip.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.response.*;
import org.atdev.artrip.controller.spec.UserSpecification;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.UserHistoryService;
import org.atdev.artrip.service.UserService;
import org.atdev.artrip.controller.dto.request.NicknameRequest;
import org.atdev.artrip.service.dto.result.ExhibitRecentResult;
import org.atdev.artrip.service.dto.result.MypageResult;
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
    public ResponseEntity<ProfileImageResponse> updateUserImage(
            @LoginUser Long userId,
            @RequestPart("image") MultipartFile image){

        userService.updateUserImage(userId, image);

        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping("/image")
    public ResponseEntity<Void> deleteUserImage(
            @LoginUser Long userId){

        userService.deleteUserImage(userId);

        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping
    public ResponseEntity<NicknameResponse> updateNickname(
            @LoginUser Long userId,
            @RequestBody @Valid NicknameRequest request) {

        userService.updateNickName(userId,request.NickName());

        return ResponseEntity.noContent().build();
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
            @LoginUser Long userId){

        List<ExhibitRecentResult> results = userHistoryService.getRecentViews(userId);

        return ResponseEntity.ok(ExhibitRecentResponse.from(results));
    }

}
