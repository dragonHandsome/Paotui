package org.dragon.paotui.controller;

import org.dragon.paotui.enumeration.ErrorResp;
import org.dragon.paotui.payload.UserDetailReq;
import org.dragon.paotui.payload.UserDetailResp;
import org.dragon.paotui.payload.ViewData;
import org.dragon.paotui.pojo.WechatUser;
import org.dragon.paotui.pojo.WechatUserDetail;
import org.dragon.paotui.security.CurrentUser;
import org.dragon.paotui.service.FileUpLoadService;
import org.dragon.paotui.service.WechatUserService;
import org.dragon.paotui.utils.MyDateFormat;
import org.dragon.paotui.utils.MyLogUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class WechatUserController {
    @Autowired
    WechatUserService wechatUserService;
    @Autowired
    FileUpLoadService fileUpLoadService;
    @RequestMapping(method = RequestMethod.POST)
    public ViewData<?> updateUserInfo(@RequestBody WechatUser wechatUser, @CurrentUser WechatUser curUser){
        wechatUser.setUsername(curUser.getUsername());
        wechatUser.setId(curUser.getId());
        try{
            wechatUserService.updateWechatUser(wechatUser);
            return ViewData.ok();
        }catch (Exception e){
            e.printStackTrace();
            return ViewData.error(ErrorResp.UPDATE_USER_ERROR);
        }
    }
    @RequestMapping(value = "/detail",method = RequestMethod.POST)
    public ViewData<?> updateUserDetail(@RequestBody UserDetailReq userDetailReq, @CurrentUser WechatUser curUser){

        long userId = curUser.getId();
        WechatUserDetail wechatUserDetail = WechatUserDetail.builder().userId(userId)
                .birthday(userDetailReq.getBirthday())
                .motto(userDetailReq.getMotto())
                .school(userDetailReq.getSchool())
                .phone(userDetailReq.getPhone())
                .avatar(userDetailReq.getAvatar())
                .backgroundImage(userDetailReq.getBackgroundImage())
                .build();
        try{
            wechatUserService.updateUserDetail(wechatUserDetail);
            return ViewData.ok();
        }catch (Exception e){
            e.printStackTrace();
            return ViewData.error(ErrorResp.UPDATE_USER_DETAIL_ERROR);
        }
    }
    @RequestMapping(value = "/detail",method = RequestMethod.GET)
    public ViewData<?> getUserDetail(@RequestParam(value = "id", required = false) Long id, @CurrentUser WechatUser curUser){
        try{
            if(id == null) id = curUser.getId();
            UserDetailResp userDetail = wechatUserService.findUserDetail(id);
            return ViewData.ok(userDetail);
        }catch (Exception e){
            e.printStackTrace();
            return ViewData.error(ErrorResp.GET_INFO_ERROR);
        }
    }
}
