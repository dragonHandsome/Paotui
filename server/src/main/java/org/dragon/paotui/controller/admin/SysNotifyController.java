package org.dragon.paotui.controller.admin;

import org.dragon.paotui.Aspect.NotifyWebsocket;
import org.dragon.paotui.payload.NotifyContent;
import org.dragon.paotui.payload.ViewData;
import org.dragon.paotui.utils.NotifyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/adminPage/notify")
public class SysNotifyController {
    @Autowired
    NotifyFactory notifyFactory;
    public ViewData sendNotifyTOAllOnlinePeople(String msg){
        try{
            notifyFactory.createSystemNotify(msg);
            return ViewData.ok("发送成功");
        }catch (Exception e){
            return ViewData.error("发送全体通知异常");
        }
    }
}
