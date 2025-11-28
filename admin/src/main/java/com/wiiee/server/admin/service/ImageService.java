package com.wiiee.server.admin.service;

import com.wiiee.server.admin.repository.ImageRepository;
import com.wiiee.server.admin.util.S3Uploader;
import com.wiiee.server.common.domain.common.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ImageService {

    private final S3Uploader s3Uploader;
    private final ImageRepository imageRepository;

//    public Image createNewImages(String dirName, MultipartFile imageFile) {
//        return uploadOne(dirName, imageFile);
//    }

    public Image getImageById(Long id) {
        return imageRepository.findById(id).orElseThrow();
    }

    public List<Image> findByIdsIn(List<Long> imageIds) {
        return imageRepository.findByIdsIn(imageIds);
    }

    public Image uploadOne(MultipartFile multipartFile, String dirName) throws IOException {
        String url = s3Uploader.upload(multipartFile, dirName);
        return imageRepository.save(Image.from(url));
    }
}
