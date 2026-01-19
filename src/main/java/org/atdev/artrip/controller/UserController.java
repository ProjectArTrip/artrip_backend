package org.atdev.artrip.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.response.ExhibitRecentResponse;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.UserService;
import org.atdev.artrip.controller.dto.request.NicknameRequest;
import org.atdev.artrip.controller.dto.response.MypageResponse;
import org.atdev.artrip.controller.dto.response.NicknameResponse;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonErrorCode;
import org.atdev.artrip.global.apipayload.code.status.ExhibitErrorCode;
import org.atdev.artrip.controller.dto.request.ImageResizeRequest;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/my")
public class UserController {

    private final UserService userService;

    @Operation(summary = "프로필 이미지 추가", description = "프로필 이미지를 추가합니다")
    @PostMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiErrorResponses(
            common = {CommonErrorCode._INTERNAL_SERVER_ERROR, CommonErrorCode._UNAUTHORIZED},
            user = {UserErrorCode._PROFILE_IMAGE_NOT_EXIST, UserErrorCode._USER_NOT_FOUND}
    )
    public ResponseEntity<CommonResponse<String>> getUpdateImage(
            @LoginUser Long userId,
            @RequestPart("image") MultipartFile image){

        userService.updateProfileImg(userId,image);

        return ResponseEntity.ok(CommonResponse.onSuccess("프로필 이미지 생성"));
    }

    @Operation(summary = "프로필 이미지 삭제", description = "기본 프로필 이미지로 변경됩니다")
    @DeleteMapping("/profile")
    @ApiErrorResponses(
            common = {CommonErrorCode._INTERNAL_SERVER_ERROR, CommonErrorCode._UNAUTHORIZED},
            user = {UserErrorCode._PROFILE_IMAGE_NOT_EXIST, UserErrorCode._USER_NOT_FOUND}
    )
    public ResponseEntity<CommonResponse<String>> getDeleteImage(
            @LoginUser Long userId){

        userService.deleteProfileImg(userId);

        return ResponseEntity.ok(CommonResponse.onSuccess("프로필 이미지 삭제"));
    }

    @Operation(summary = "닉네임 설정", description = "공백 입력 불가")
    @PatchMapping("/nickname")
    @ApiErrorResponses(
            common = {CommonErrorCode._INTERNAL_SERVER_ERROR, CommonErrorCode._UNAUTHORIZED},
            user = {UserErrorCode._DUPLICATE_NICKNAME, UserErrorCode._USER_NOT_FOUND, UserErrorCode._NICKNAME_BAD_REQUEST}
    )
    public ResponseEntity<CommonResponse<NicknameResponse>> updateNickname(
            @LoginUser Long userId,
            @RequestBody NicknameRequest dto) {

        NicknameResponse response = userService.updateNickName(userId, dto);

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    @Operation(summary = "마이페이지 조회", description = "닉네임, 프로필 이미지 조회")
    @GetMapping("/mypage")
//    @ApiErrorResponses(
//            common = {CommonErrorCode._INTERNAL_SERVER_ERROR, CommonErrorCode._UNAUTHORIZED},
//            user = {UserErrorCode._USER_NOT_FOUND}
//    )
    public ResponseEntity<CommonResponse<MypageResponse>> getMypage(
            @LoginUser Long userId,
            @ParameterObject ImageResizeRequest resize) {

        MypageResponse response = userService.getMypage(userId, resize);

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    @Operation(summary = "최근 본 전시", description = "최근 본 전시 20개")
    @GetMapping("/recent")
    @ApiErrorResponses(
            common = {CommonErrorCode._INTERNAL_SERVER_ERROR, CommonErrorCode._UNAUTHORIZED},
            user = {UserErrorCode._USER_NOT_FOUND},
            exhibit = {ExhibitErrorCode._EXHIBIT_NOT_FOUND}
    )
    public ResponseEntity<CommonResponse<List<ExhibitRecentResponse>>> getRecentExhibit(
            @LoginUser Long userId){

        List<ExhibitRecentResponse> responses = userService.getRecentViews(userId);

        return ResponseEntity.ok(CommonResponse.onSuccess(responses));
    }

}
