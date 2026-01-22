package org.atdev.artrip.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.response.ExhibitRecentResponse;
import org.atdev.artrip.controller.dto.response.ProfileImageResponse;
import org.atdev.artrip.controller.spec.UserSpecification;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.UserService;
import org.atdev.artrip.controller.dto.request.NicknameRequest;
import org.atdev.artrip.controller.dto.response.MypageResponse;
import org.atdev.artrip.controller.dto.response.NicknameResponse;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonErrorCode;
import org.atdev.artrip.global.apipayload.code.status.ExhibitErrorCode;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.atdev.artrip.service.dto.command.UserReadCommand;
import org.atdev.artrip.service.dto.command.NicknameCommand;
import org.atdev.artrip.service.dto.command.ProfileCommand;
import org.atdev.artrip.service.dto.result.ExhibitRecentResult;
import org.atdev.artrip.service.dto.result.MypageResult;
import org.atdev.artrip.service.dto.result.NicknameResult;
import org.atdev.artrip.service.dto.result.ProfileResult;
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

    @Override
    @PostMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProfileImageResponse> getUpdateImage(
            @LoginUser Long userId,
            @RequestPart("image") MultipartFile image){

        ProfileCommand command = ProfileCommand.of(userId,image);
        ProfileResult result = userService.updateProfileImg(command);

        return ResponseEntity.ok(ProfileImageResponse.from(result));
    }

    @Override
    @DeleteMapping("/profile-image")
    public ResponseEntity<Void> getDeleteImage(
            @LoginUser Long userId){

        ProfileCommand command = ProfileCommand.of(userId);

        userService.deleteProfileImg(command);

        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping()
    public ResponseEntity<NicknameResponse> updateNickname(
            @LoginUser Long userId,
            @RequestBody NicknameRequest request) {

        NicknameCommand command = request.toCommand(request,userId);
        NicknameResult response = userService.updateNickName(command);

        return ResponseEntity.ok(NicknameResponse.from(response));
    }

    @Operation(summary = "마이페이지 조회", description = "닉네임, 프로필 이미지 조회")
    @GetMapping()
    @ApiErrorResponses(
            common = {CommonErrorCode._INTERNAL_SERVER_ERROR, CommonErrorCode._UNAUTHORIZED},
            user = {UserErrorCode._USER_NOT_FOUND}
    )
    public ResponseEntity<MypageResponse> getMypage(
            @LoginUser Long userId) {

        UserReadCommand command = UserReadCommand.from(userId);
        MypageResult response = userService.getMypage(command);

        return ResponseEntity.ok(MypageResponse.from(response));
    }

    @Operation(summary = "최근 본 전시", description = "최근 본 전시 20개")
    @GetMapping("/recent-exhibits")
    @ApiErrorResponses(
            common = {CommonErrorCode._INTERNAL_SERVER_ERROR, CommonErrorCode._UNAUTHORIZED},
            user = {UserErrorCode._USER_NOT_FOUND},
            exhibit = {ExhibitErrorCode._EXHIBIT_NOT_FOUND}
    )
    public ResponseEntity<List<ExhibitRecentResponse>> getRecentExhibit(
            @LoginUser Long userId){

        UserReadCommand command = UserReadCommand.from(userId);
        List<ExhibitRecentResult> responses = userService.getRecentViews(command);

        return ResponseEntity.ok(ExhibitRecentResponse.from(responses));
    }

}
