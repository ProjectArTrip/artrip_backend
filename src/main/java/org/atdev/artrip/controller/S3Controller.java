package org.atdev.artrip.controller;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.constants.FileFolder;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.atdev.artrip.global.apipayload.code.status.CommonErrorCode;
import org.atdev.artrip.global.apipayload.code.status.S3ErrorCode;
import org.atdev.artrip.global.s3.service.S3Service;
import org.atdev.artrip.controller.dto.request.ImageDeleteRequest;
import org.atdev.artrip.global.swagger.ApiErrorResponses;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/s3")
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/upload")
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST},
            s3 = {S3ErrorCode._NOT_EXIST_FILE, S3ErrorCode._NOT_EXIST_FILE_EXTENSION, S3ErrorCode._IO_EXCEPTION_UPLOAD_FILE, S3ErrorCode._INVALID_URL_FORMAT}
    )
    public CommonResponse<List<String>> s3Upload(@RequestPart(value = "image") List<MultipartFile> multipartFile) {
        List<String> upload = s3Service.uploadFiles(multipartFile, FileFolder.POSTERS);
        return CommonResponse.onSuccess(upload);
    }

    @DeleteMapping("/delete")
    @ApiErrorResponses(
            common = {CommonErrorCode._BAD_REQUEST},
            s3 = {S3ErrorCode._INVALID_URL_FORMAT, S3ErrorCode._IO_EXCEPTION_DELETE_FILE}
    )
    public CommonResponse<String> s3Delete(@RequestBody ImageDeleteRequest imageDeleteRequest) {
        s3Service.delete(imageDeleteRequest.getImageUrls());
        return CommonResponse.onSuccess("이미지 삭제 성공");
    }

}