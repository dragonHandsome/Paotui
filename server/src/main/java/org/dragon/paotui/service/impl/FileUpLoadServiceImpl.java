package org.dragon.paotui.service.impl;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.dragon.paotui.service.FileUpLoadService;
import org.dragon.paotui.utils.MyLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.UUID;

@Service
public class FileUpLoadServiceImpl implements FileUpLoadService {
    final static String FILE_DIR = "/uploads/" ;
    final static String ROOT = "C:\\Users\\Beautiful\\Pictures\\paotui\\images";
    @Autowired
    HttpServletRequest request;
    @Override
    public String uploadFile(MultipartFile file) {
        String originName = file.getOriginalFilename();
        //String contentType = file.getContentType().replaceFirst(".*?/","");
        String substring = originName.substring(originName.lastIndexOf(".") + 1).toLowerCase();

        File dir = new File(ROOT);
        if(!dir.exists()) dir.mkdirs();
        //String fileName = UUID.randomUUID().toString().replaceAll("-","").toLowerCase();
        String fileName = UUID.randomUUID().toString().replaceAll("-","").toLowerCase();
        String relativePath = fileName +"."+ substring;
        try {
            //file.transferTo(new File(ROOT, relativePath));
            Thumbnails.of(file.getInputStream())
                    .scale(.8)
                    .outputQuality(0.75f)
                    .toFile(new File(ROOT, relativePath));
            Thumbnails.of(file.getInputStream())
                    .sourceRegion(Positions.TOP_CENTER, 400, 400)
                    .size(200, 200).keepAspectRatio(false)
                    .toFile(new File(ROOT, fileName + "-avatar." + substring));
            Thumbnails.of(file.getInputStream())
                    .scale(0.25f)
                    .outputQuality(0.25f)
                    .toFile(new File(ROOT, fileName + "-min." + substring));
        } catch (IOException e) {
            throw new RuntimeException("上传文件失败");
        }
        return FILE_DIR + relativePath;
    }

    /**
     *
     * Thumbnails.of(file.getInputStream())
     * @param imgUrl
     * @return
     */
    @Override
    public byte[] loadImage(String imgUrl){
        //最大就2M
        byte[] imgMax = new byte[2*1024*1024];
        try(FileInputStream fis = new FileInputStream(new File(ROOT, imgUrl));
            BufferedInputStream bis = new BufferedInputStream(fis);
        ){
            int len;
            int pos = 0;
            while ((len = bis.read(imgMax, pos, 2048)) > 0){
                pos += len;
            }
            int size = pos;
            byte[] imgOut = new byte[size];
            System.arraycopy(imgMax, 0, imgOut, 0, size);
            return imgOut;
        }catch (IOException e) {
            MyLogUtil.error("img加载失败");
        }
        return null;
    }

}
