package org.dragon.paotui.controller;

import lombok.Getter;
import lombok.Setter;
import org.dragon.paotui.enumeration.ErrorResp;
import org.dragon.paotui.payload.Like;
import org.dragon.paotui.payload.UserDetailResp;
import org.dragon.paotui.payload.ViewData;
import org.dragon.paotui.pojo.WechatUser;
import org.dragon.paotui.security.CurrentUser;
import org.dragon.paotui.service.WechatUserService;
import org.dragon.paotui.utils.MyLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/userList")
public class UserListController {
    @Autowired
    private WechatUserService wechatUserService;
    @RequestMapping(method = RequestMethod.GET)
    public ViewData<?> getUserList(@CurrentUser WechatUser user){
        try{
            List<UserDetailResp> users = wechatUserService.findLikedUserList(user.getId());
            return ViewData.ok(users);
        }catch (Exception e){
            return ViewData.error(ErrorResp.LIKE_ERROR);
        }
    }
    @RequestMapping(value = "/like",method = RequestMethod.POST)
    public ViewData<?> likeTA(@RequestBody Like like, @CurrentUser WechatUser user){
        try{
            requireNotCurrentUser(user.getId(), like.getToUserId());
            wechatUserService.toggleLike(user.getId(), like.getToUserId());
            return ViewData.ok();
        }catch (Exception e){
            return ViewData.error(ErrorResp.LIKE_ERROR);
        }
    }
    @RequestMapping(value = "/like",method = RequestMethod.GET)
    public ViewData<?> isLike(Long id, @CurrentUser WechatUser user){
        try{
            requireNotCurrentUser(id, user.getId());
            Boolean isLike = wechatUserService.isLike(user.getId(), id);
            return ViewData.ok(isLike);
        }catch (Exception e){
            return ViewData.error(ErrorResp.LIKE_ERROR);
        }
    }
    @SuppressWarnings("deprecation")
    private void requireNotCurrentUser(Long fromUserId, Long toUserId) {
        if(toUserId == null) {
            MyLogUtil.error("toUserId is null");
        }
        Assert.isTrue(fromUserId != toUserId);
    }
}
