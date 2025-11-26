package org.atdev.artrip.global.s3;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/s3")
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/upload")
    public CommonResponse<List<String>> s3Upload(@RequestPart(value = "image") List<MultipartFile> multipartFile) {
        List<String> upload = s3Service.upload(multipartFile);
        return CommonResponse.onSuccess(upload);
    }

    @DeleteMapping("/delete")
    public CommonResponse<String> s3Delete(@RequestBody ImageDeleteRequest imageDeleteRequest) {
        s3Service.delete(imageDeleteRequest.getImageUrls());
        return CommonResponse.onSuccess("이미지 삭제 성공");
    }

}