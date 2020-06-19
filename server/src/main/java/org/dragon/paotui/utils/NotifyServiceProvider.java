package org.dragon.paotui.utils;

import org.aspectj.lang.annotation.Pointcut;
import org.dragon.paotui.enumeration.Action;
import org.dragon.paotui.enumeration.NotifyType;
import org.dragon.paotui.payload.NotifyContent;
import org.dragon.paotui.payload.UserDetailResp;
import org.dragon.paotui.pojo.Notify;
import org.dragon.paotui.pojo.WechatUser;
import org.dragon.paotui.service.AdminService;
import org.dragon.paotui.service.NotifyService;
import org.dragon.paotui.service.WechatUserService;
import org.springframework.stereotype.Component;


@Component
public class NotifyServiceProvider {

    public static NotifyService notifyService;
    public static WechatUserService userService;
    public static AdminService adminService;

}
