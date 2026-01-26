package org.atdev.artrip.global.s3.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.constants.FileFolder;
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


    public String uploadFile(MultipartFile file, FileFolder folder) {
        validateFile(file);
        return uploadToS3(file, folder.getPath());
    }

    public List<String> uploadFiles(List<MultipartFile> files, FileFolder folder) {

        return files.stream()
                .map(file -> uploadFile(file, folder))
                .toList();
    }
    //11111111111111111111111111111111111111111111111111111111

//    public String uploadPoster(MultipartFile file) {
//        return uploadToFolder(file, FOLDER_POSTERS);
//    }
//
//    public List<String> uploadPoster(List<MultipartFile> files) {
//        return files.stream()
//                .map(this::uploadPoster)
//                .toList();
//    }
//
//    public String uploadReview(MultipartFile  file) {
//        return uploadToFolder(file, FOLDER_REVIEWS);
//    }
//
//    public List<String> uploadReviews(List<MultipartFile> files) {
//        return files.stream()
//                .map(this::uploadReview)
//                .toList();
//    }
//
//    public String uploadProfile(MultipartFile file) {
//        return uploadToFolder(file, FOLDER_PROFILES);
//    }


    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new GeneralException(S3ErrorCode._NOT_EXIST_FILE);
        }

        String filename = file.getOriginalFilename();

        if (filename == null || filename.isEmpty()) {
            throw new GeneralException(S3ErrorCode._NOT_EXIST_FILE);
        }

        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new GeneralException(S3ErrorCode._NOT_EXIST_FILE_EXTENSION);
        }

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

    private String uploadToS3(MultipartFile file, String folder) {
        String originalFilename = file.getOriginalFilename();
        String extension = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf(".") + 1);
        String s3Key = String.format("%s/%s.%s", folder, UUID.randomUUID().toString().substring(0, 10), extension);

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType("image/" + extension)
                    .contentLength(file.getSize())
                    .cacheControl("public, max-age=31536000")
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            throw new GeneralException(S3ErrorCode._IO_EXCEPTION_UPLOAD_FILE);
        }

        return buildImageUrl(s3Key);
    }

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
            DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(delete -> delete.objects(
                            keys.stream()
                                    .map(key -> ObjectIdentifier.builder().key(key).build())
                                    .toList()
                    ))
                    .build();
            s3Client.deleteObjects(deleteObjectsRequest);
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            throw new GeneralException(S3ErrorCode._IO_EXCEPTION_DELETE_FILE);
        }
    }

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

    private String getKeyFromImageUrls(String imageUrl) {
        try {
            URL url = new URI(imageUrl).toURL();
            String decodedKey = URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8);
            return decodedKey.substring(1);
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

//    private String generateSignature(String key, int w, int h, String f, long ts) {
//        try {
//            String data = String.format("%s:%d:%d:%s:%d", key, w, h, f, ts);
//            Mac mac = Mac.getInstance("HmacSHA256");
//            SecretKeySpec secretKeySpec = new SecretKeySpec(resizeSecretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
//            mac.init(secretKeySpec);
//            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
//            return HexFormat.of().formatHex(hash);
//        } catch (Exception e) {
//            log.error("서명 생성 실패", e);
//            throw new GeneralException(S3ErrorCode._IO_EXCEPTION_UPLOAD_FILE);
//        }
//    }
//
//    public String buildResizeUrl(String originalUrl, Integer width, Integer height, String format) {
//        if (originalUrl == null || originalUrl.isEmpty()) {
//            return originalUrl;
//        }
//
//        if (cloudFrontDomain == null || cloudFrontDomain.isEmpty()) {
//            return originalUrl;
//        }
//
//        if (resizeSecretKey == null || resizeSecretKey.isEmpty()) {
//            return null;
//        }
//
//        try {
//            String s3Key = getKeyFromImageUrls(originalUrl);
//            int w = (width != null && width > 0) ? Math.min(width, 1200) : 1;
//            int h = (height != null && height > 0) ? Math.min(height, 1200) : 1;
//            String f = (format != null && !format.isEmpty()) ? format : "webp";
//            long ts = System.currentTimeMillis() / 1000;
//
//            String sig = generateSignature(s3Key, w, h, f, ts);
//
//            return String.format("https://%s?key=%s&w=%d&h=%d&f=%s&ts=%d&sig=%s",
//                    cloudFrontDomain,
//                    URLEncoder.encode(s3Key, StandardCharsets.UTF_8),
//                    w, h, f, ts, sig);
//        } catch (Exception e) {
//            log.warn("리사이징 URL 생성 실패: {}", originalUrl);
//            return originalUrl;
//        }
//    }

//    private String uploadToFolder(MultipartFile file, String folder) {
//        validateFile(file);
//        return uploadToS3(file, folder);
//    }

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