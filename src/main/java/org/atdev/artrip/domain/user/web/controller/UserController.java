package org.atdev.artrip.domain.user.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.keyword.web.dto.KeywordRequest;
import org.atdev.artrip.domain.user.service.UserService;
import org.atdev.artrip.domain.user.web.dto.requestDto.NicknameRequestDto;
import org.atdev.artrip.domain.user.web.dto.responseDto.NicknameResponseDto;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonError;
import org.atdev.artrip.global.apipayload.code.status.KeywordError;
import org.atdev.artrip.global.apipayload.code.status.UserError;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/my")
public class UserController {

    private final UserService userService;

    @Operation(summary = "나의 취향 분석", description = "내가 선택한 키워드 선택 저장")
    @ApiErrorResponses(
            common = {CommonError._INTERNAL_SERVER_ERROR, CommonError._UNAUTHORIZED},
            keyword = {KeywordError._KEYWORD_INVALID_REQUEST, KeywordError._KEYWORD_SELECTION_LIMIT_EXCEEDED, KeywordError._KEYWORD_NOT_FOUND}
    )
    @PostMapping("/keywords")
    public ResponseEntity<CommonResponse<Void>> saveUserKeywords(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody KeywordRequest request) {

        Long userId = Long.parseLong(userDetails.getUsername()); // subject → userId형변환

        userService.saveUserKeywords(userId, request.getKeywordIds());
        return ResponseEntity.ok(CommonResponse.onSuccess(null));
    }

    @PostMapping("/profile")
    @ApiErrorResponses(
            common = {CommonError._INTERNAL_SERVER_ERROR, CommonError._UNAUTHORIZED},
            user = {UserError._PROFILE_IMAGE_NOT_EXIST,UserError._USER_NOT_FOUND}
    )
    public ResponseEntity<CommonResponse<String>> getUpdateImage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("image") MultipartFile image){

        Long userId = Long.parseLong(userDetails.getUsername());

        userService.updateProfileImg(userId,image);

        return ResponseEntity.ok(CommonResponse.onSuccess("프로필 이미지 생성"));
    }

    @DeleteMapping("/profile")
    @ApiErrorResponses(
            common = {CommonError._INTERNAL_SERVER_ERROR, CommonError._UNAUTHORIZED},
            user = {UserError._PROFILE_IMAGE_NOT_EXIST,UserError._USER_NOT_FOUND}
    )
    public ResponseEntity<CommonResponse<String>> getDeleteImage(
            @AuthenticationPrincipal UserDetails userDetails){

        Long userId = Long.parseLong(userDetails.getUsername());

        userService.deleteProfileImg(userId);

        return ResponseEntity.ok(CommonResponse.onSuccess("프로필 이미지 삭제"));
    }

    @PatchMapping("/nickname")
    @ApiErrorResponses(
            common = {CommonError._INTERNAL_SERVER_ERROR, CommonError._UNAUTHORIZED},
            user = {UserError._DUPLICATE_NICKNAME,UserError._USER_NOT_FOUND,UserError._NICKNAME_EMPTY}
    )
    public ResponseEntity<CommonResponse<NicknameResponseDto>> updateNickname(
            @AuthenticationPrincipal UserDetails user,
            @RequestBody NicknameRequestDto dto) {

        Long userId = Long.valueOf(user.getUsername());

        NicknameResponseDto response = userService.updateNickName(userId, dto);

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }


}
