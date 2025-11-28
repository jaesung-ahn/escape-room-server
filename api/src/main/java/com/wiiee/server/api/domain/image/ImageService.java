package com.wiiee.server.api.domain.image;

import com.wiiee.server.api.infrastructure.aws.S3Util;
import com.wiiee.server.common.domain.common.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ImageService {

    private final S3Util s3Util;
    private final ImageRepository imageRepository;

    public Image createNewImage(String dirName, MultipartFile imageFile) {
        return uploadOne(dirName, imageFile);
    }

    public Image createNewImage(String url) {
        return imageRepository.save(Image.from(url));
    }

    public Image getImageById(Long id) {
        return imageRepository.findById(id)
                .orElse(new Image(""));
    }

    public List<Image> findByIdsIn(List<Long> imageIds) {
        return imageRepository.findByIdsIn(imageIds);
    }

    private Image uploadOne(String dirName, MultipartFile multipartFile) {
        String url = s3Util.upload(dirName, multipartFile);
        return imageRepository.save(Image.from(url));
    }
}
