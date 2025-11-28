package com.wiiee.server.api.application.image;

import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

@Value
public class ImagePostRequestDTO {

    @Value
    public static class File {
        MultipartFile imageFile;
    }

    @Value
    public static class URL {
        String url;
    }

}
