package org.atdev.artrip.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import org.atdev.artrip.controller.dto.request.NicknameRequest;
import org.atdev.artrip.controller.dto.response.ExhibitRecentResponse;
import org.atdev.artrip.controller.dto.response.MypageResponse;
import org.atdev.artrip.controller.dto.response.NicknameResponse;
import org.atdev.artrip.controller.dto.response.ProfileImageResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonErrorCode;
import org.atdev.artrip.global.apipayload.code.status.ExhibitErrorCode;
import org.atdev.artrip.global.apipayload.code.status.UserErrorCode;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserSpecification {

    @Operation(summary = "프로필 이미지 추가", description = "프로필 이미지를 추가합니다")
    @ApiErrorResponses(
            common = {CommonErrorCode._INTERNAL_SERVER_ERROR, CommonErrorCode._UNAUTHORIZED},
            user = {UserErrorCode._PROFILE_IMAGE_NOT_EXIST, UserErrorCode._USER_NOT_FOUND}
    )
    public ResponseEntity<ProfileImageResponse> getUpdateImage(
            @LoginUser Long userId,
            @RequestPart("image") MultipartFile image);


    @Operation(summary = "프로필 이미지 삭제", description = "기본 프로필 이미지로 변경됩니다")
    @ApiErrorResponses(
            common = {CommonErrorCode._INTERNAL_SERVER_ERROR, CommonErrorCode._UNAUTHORIZED},
            user = {UserErrorCode._PROFILE_IMAGE_NOT_EXIST, UserErrorCode._USER_NOT_FOUND}
    )
    public ResponseEntity<Void> getDeleteImage(
            @LoginUser Long userId);


    @Operation(summary = "닉네임 설정", description = "공백 입력 불가")
    @ApiErrorResponses(
            common = {CommonErrorCode._INTERNAL_SERVER_ERROR, CommonErrorCode._UNAUTHORIZED},
            user = {UserErrorCode._DUPLICATE_NICKNAME, UserErrorCode._USER_NOT_FOUND, UserErrorCode._NICKNAME_BAD_REQUEST}
    )
    public ResponseEntity<NicknameResponse> updateNickname(
            @LoginUser Long userId,
            @RequestBody NicknameRequest dto);

    @Operation(summary = "마이페이지 조회", description = "닉네임, 프로필 이미지 조회")
    @ApiErrorResponses(
            common = {CommonErrorCode._INTERNAL_SERVER_ERROR, CommonErrorCode._UNAUTHORIZED},
            user = {UserErrorCode._USER_NOT_FOUND}
    )
    public ResponseEntity<MypageResponse> getMypage(
            @LoginUser Long userId);

    @Operation(summary = "최근 본 전시", description = "최근 본 전시 20개")
    @ApiErrorResponses(
            common = {CommonErrorCode._INTERNAL_SERVER_ERROR, CommonErrorCode._UNAUTHORIZED},
            user = {UserErrorCode._USER_NOT_FOUND},
            exhibit = {ExhibitErrorCode._EXHIBIT_NOT_FOUND}
    )
    public ResponseEntity<List<ExhibitRecentResponse>> getRecentExhibit(
            @LoginUser Long userId);
}
