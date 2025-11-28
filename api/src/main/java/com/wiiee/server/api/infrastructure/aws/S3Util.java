package com.wiiee.server.api.infrastructure.aws;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Util {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    private final static String IMAGE_EXTENSION = ".jpg";

    public String upload(String dirName, MultipartFile multipartFile) {
        File uploadFile = convert(multipartFile)
                .orElseThrow(() ->
                        new IllegalArgumentException("[S3Uploader] MultipartFile -> File 변환 실패했습니다."));
        return upload(dirName, uploadFile);
    }

    public void delete(String key) {
        amazonS3Client.deleteObject(this.bucket, key);
    }

    // s3로 파일 업로드
    private String upload(String dirName, File uploadFile) {
        // S3에 저장된 파일 이름
        String fileName = dirName.concat("/").concat(uploadFile.getName());
        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    // s3 업로드
    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // 로컬에 저장된 이미지 삭제
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("File delete success");
            return;
        }
        log.info("File delete fail");
    }

    // 로컬에 이미지 업로드
    private Optional<File> convert(MultipartFile file) {
        File convertFile = new File(System.getProperty("user.dir").concat("/").concat(String.valueOf(UUID.randomUUID())).concat(IMAGE_EXTENSION));
        // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가)
        try {
            if (convertFile.exists()) {
                convertFile.delete();
            }
            if (convertFile.createNewFile()) {
                try (FileOutputStream fos = new FileOutputStream(convertFile)) {  //  FileOutputStream : 데이터를 파일에 스트림으로 저장하기 위함
                    fos.write(file.getBytes());
                } catch (IOException e) {
                    log.info("[S3Uploader] 로컬에 이미지 업로드 실패하였습니다.");
                    e.printStackTrace();
                }
                return Optional.of(convertFile);
            }
        } catch (IOException e) {
            log.info("[S3Uploader] 로컬에 파일 열기 작업을 실패하였습니다.");
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void deleteAll() {
        deleteDir("");
    }

    public void deleteDir(String dirName) {
        ObjectListing objectList = amazonS3Client.listObjects(this.bucket, dirName);
        List<S3ObjectSummary> objectSummaryList = objectList.getObjectSummaries();
        String[] keyList = new String[objectSummaryList.size()];
        if (keyList.length > 0) {
            int count = 0;
            for (S3ObjectSummary summary : objectSummaryList) {
                keyList[count++] = summary.getKey();
            }
            if (count > 0) {
                amazonS3Client.deleteObjects(new DeleteObjectsRequest(this.bucket)
                        .withKeys(keyList));
            }
        }
    }
}
