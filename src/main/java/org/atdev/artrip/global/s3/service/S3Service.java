package org.atdev.artrip.global.s3.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.global.apipayload.code.status.S3ErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class S3Service {
    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${spring.cloud.aws.cloudfront.domain:}")
    private String cloudFrontDomain;

    @Value("${image.upload.max-size:2097152}")
    private long maxFileSize;

    @Value("${image.resize.secret-key}")
    private String resizeSecretKey;

    private static final String FOLDER_POSTERS = "posters";
    private static final String FOLDER_REVIEWS = "reviews";
    private static final String FOLDER_PROFILES = "profiles";

    public String uploadPoster(MultipartFile file) {
        return uploadToFolder(file, FOLDER_POSTERS);
    }

    // S3에 저장된 이미지 객체의 public url을 반환
    public List<String> uploadPoster(List<MultipartFile> files) {
        // 각 파일을 업로드하고 url을 리스트로 반환
        return files.stream()
                .map(this::uploadPoster)
                .toList();
    }

    public String uploadReview(MultipartFile  file) {
        return uploadToFolder(file, FOLDER_REVIEWS);
    }

    public List<String> uploadReviews(List<MultipartFile> files) {
        return files.stream()
                .map(this::uploadReview)
                .toList();
    }

    public String uploadProfile(MultipartFile file) {
        return uploadToFolder(file, FOLDER_PROFILES);
    }

    // 파일 유효성 검증
    private void validateFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        // 파일 존재 유무 검증
        if (filename == null || filename.isEmpty()) {
            throw new GeneralException(S3ErrorCode._NOT_EXIST_FILE);
        }

        // 확장자 존재 유무 검증
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new GeneralException(S3ErrorCode._NOT_EXIST_FILE_EXTENSION);
        }

        // 허용되지 않는 확장자 검증
        String fileExtension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        List<String> allowedExtensions = Arrays.asList("jpg", "png", "jpeg", "webp");

        if (!allowedExtensions.contains(fileExtension)) {
            throw new GeneralException(S3ErrorCode._INVALID_FILE_EXTENSION);
        }

        if (file.getSize() > maxFileSize) {
            log.warn("파일 크기 초과 : {}bytes (최대 : {}bytes", file.getSize(), maxFileSize);
            throw new GeneralException(S3ErrorCode._FILE_SIZE_EXCEEDED);
        }
    }

    // 직접적으로 S3에 업로드
    private String uploadToS3(MultipartFile file, String folder) {
        // 원본 파일 명
        String originalFilename = file.getOriginalFilename();
        // 확장자 명
        String extension = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf(".") + 1);
        // 변경된 파일
        String s3Key = String.format("%s/%s.%s", folder, UUID.randomUUID().toString().substring(0, 10), extension);

        // 이미지 파일 -> InputStream 변환
        try (InputStream inputStream = file.getInputStream()) {
            // PutObjectRequest 객체 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName) // 버킷 이름
                    .key(s3Key) // 저장할 파일 이름
                    .contentType("image/" + extension) // 이미지 MIME 타입
                    .contentLength(file.getSize()) // 파일 크기
                    .cacheControl("public, max-age=31536000")
                    .build();
            // S3에 이미지 업로드
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            throw new GeneralException(S3ErrorCode._IO_EXCEPTION_UPLOAD_FILE);
        }

        // public url 반환
        return buildImageUrl(s3Key);
    }

    // 이미지의 public url을 이용하여 S3에서 해당 이미지를 제거, getKeyFromImageAddress 메서드를 호출하여 삭제에 필요한 key 획득
    public void delete(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }

        List<String> keys = imageUrls.stream()
                .filter(url -> url != null && !url.isBlank())
                .map(this::getKeyFromImageUrls)
                .toList();

        if (keys.isEmpty()) {
            return;
        }

        try {
            // S3에서 파일을 삭제하기 위한 요청 객체 생성
            DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                    .bucket(bucketName) // S3 버킷 이름 지정
                    .delete(delete -> delete.objects(
                            // S3 객체들을 삭제할 객체 목록을 생성
                            keys.stream()
                                    .map(key -> ObjectIdentifier.builder().key(key).build())
                                    .toList()
                    ))
                    .build();
            s3Client.deleteObjects(deleteObjectsRequest); // S3에서 객체 삭제
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            throw new GeneralException(S3ErrorCode._IO_EXCEPTION_DELETE_FILE);
        }
    }

//단일 delete
    public void delete(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }
        try {
            String key = getKeyFromImageUrls(imageUrl);

            s3Client.deleteObject(builder -> builder
                    .bucket(bucketName)
                    .key(key)
            );

        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            throw new GeneralException(S3ErrorCode._IO_EXCEPTION_DELETE_FILE);
        }
    }

    // 삭제에 필요한 key 반환
    private String getKeyFromImageUrls(String imageUrl) {
        try {
            URL url = new URI(imageUrl).toURL(); // 인코딩된 주소를 URI 객체로 변환 후 URL 객체로 변환
            String decodedKey = URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8);// URI에서 경로 부분을 가져와 URL 디코딩을 통해 실제 키로 변환
            return decodedKey.substring(1); // 경로 앞에 '/'가 있으므로 이를 제거한 뒤 반환
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            throw new GeneralException(S3ErrorCode._INVALID_URL_FORMAT);
        }
    }

    private String buildImageUrl(String s3Key) {
        if (cloudFrontDomain != null && !cloudFrontDomain.isEmpty()) {
            return String.format("https://%s/%s", cloudFrontDomain, s3Key);
        }
        return s3Client.utilities().getUrl(url -> url.bucket(bucketName).key(s3Key)).toString();
    }

    private String generateSignature(String key, int w, int h, String f, long ts) {
        try {
            String data = String.format("%s:%d:%d:%s:%d", key, w, h, f, ts);
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(resizeSecretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            log.error("서명 생성 실패", e);
            throw new GeneralException(S3ErrorCode._IO_EXCEPTION_UPLOAD_FILE);
        }
    }

    private String uploadToFolder(MultipartFile file, String folder) {
        validateFile(file);
        return uploadToS3(file, folder);
    }

    public String uploadFromExternalUrl(String externalUrl, String folder){
        if (externalUrl == null || externalUrl.isBlank()) {
            return null;
        }

        if (isInternalUrl(externalUrl))  {
            return externalUrl;
        }


        try {
            URL url = new URI(externalUrl).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setInstanceFollowRedirects(true);

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
            responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                    responseCode == 307 || responseCode == 300
            ) {
                String redirectUrl = connection.getHeaderField("Location");

                if (redirectUrl != null && !redirectUrl.isBlank()) {
                    // 리다이렉트 URL로 재귀 호출
                    return uploadFromExternalUrl(redirectUrl, folder);
                }
            }

            if (responseCode != 200) {
                return externalUrl;
            }

            String contentType = connection.getContentType();
            String extension = getExtensionFromContentType(contentType);
            if (extension == null) {
                extension = getExtensionFromUrl(externalUrl);
            }

            String s3Key = String.format("%s/%s.%s", folder, UUID.randomUUID().toString().substring(0, 10), extension);
            try (InputStream inputStream = connection.getInputStream()){
                byte[] imageBytes = inputStream.readAllBytes();

                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key)
                        .contentType(contentType != null ? contentType :  "image/" + extension)
                        .contentLength((long) imageBytes.length)
                        .cacheControl("public, max-age=31536000")
                        .build();

                s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));

                return buildImageUrl(s3Key);

            }
        } catch (Exception e) {
            log.error("외부 이미지 업로드 실패 - URL: {}, 에러: {}", externalUrl, e.getMessage());
            return externalUrl;
        }
    }

    private String getExtensionFromUrl(String url) {
        try {
            String path = new URI(url).getPath();
            int lastDot = path.lastIndexOf('.');
            if (lastDot > 0) {
                return path.substring(lastDot + 1).toLowerCase();
            }
        } catch (Exception ignored) {}
        return "jpg";
    }

    private String getExtensionFromContentType(String contentType) {
        if (contentType == null) return null;
        return switch (contentType.toLowerCase()) {
            case "image/jpeg", "image/jpg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            case "image/gif" -> "gif";
            default -> null;
        };
    }

    public boolean isInternalUrl(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        return url.contains("s3.ap-northeast-2.amazonaws.com") ||
                url.contains("cloudfront.net") ||
                url.contains(bucketName);
    }

    public String uploadPosterFromExternalUrl(String externalUrl) {
        return uploadFromExternalUrl(externalUrl, FOLDER_POSTERS);
    }

}