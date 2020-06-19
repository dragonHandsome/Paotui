package org.dragon.paotui.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUpLoadService {
    /**
     *
     * @param img
     * @return imgUrl
     */
    String uploadFile(MultipartFile img);

    byte[] loadImage(String imgUrl);
}
