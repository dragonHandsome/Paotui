package org.dragon.paotui.controller;

import org.dragon.paotui.enumeration.ErrorResp;
import org.dragon.paotui.payload.ViewData;
import org.dragon.paotui.service.FileUpLoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileUploadController {
    @Autowired
    FileUpLoadService fileUpLoadService;
    @RequestMapping(value = {"uploadFile", "/adminPage/uploadFile"},method = RequestMethod.POST)
    public ViewData<?> updateUserBackground(MultipartFile file){
        try{
            String imgUrl = fileUpLoadService.uploadFile(file);
            return ViewData.ok(imgUrl);
        }catch (Exception e){
            e.printStackTrace();
            return ViewData.error(ErrorResp.UPDATE_USER_DETAIL_ERROR);
        }
    }
    @RequestMapping(value = "/uploads/{img}",method = RequestMethod.GET,
            produces = {"image/jpeg","application/x-jpeg","image/png"}
    )
    public byte[] updateUserBackground(@PathVariable String img){
        try{
            return fileUpLoadService.loadImage(img);
        }catch (Exception e){
            return fileUpLoadService.loadImage("不知火舞_魅语.jpg");
        }
    }
}
