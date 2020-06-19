package org.dragon.paotui.controller;

import org.dragon.paotui.payload.NotifyChatReq;
import org.dragon.paotui.payload.ViewData;
import org.dragon.paotui.pojo.Notify;
import org.dragon.paotui.pojo.WechatUser;
import org.dragon.paotui.security.CurrentUser;
import org.dragon.paotui.service.NotifyService;
import org.dragon.paotui.utils.NotifyFactory;
import org.dragon.paotui.utils.NotifyServiceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notify")
public class NotifyController {
    @Autowired
    NotifyService notifyService;
    @Autowired
    NotifyFactory notifyFactory;
    @GetMapping("/remind")
    public ViewData isReadAndCompleted(Long notifyId){
        try{
            notifyService.isRead(notifyId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ViewData.error("fail");
    }
    @PutMapping("/chat")
    public ViewData addNotify(@RequestBody NotifyChatReq chatReq, @CurrentUser WechatUser user){
        try{
            notifyFactory.init(user.getId(), chatReq.getToUserId());
            Notify chatNotify = notifyFactory.createChatNotify(chatReq.getContent());
            return ViewData.ok(chatNotify);
        }catch (Exception e){
            e.printStackTrace();
            return ViewData.error(e.getMessage());
        }
    }
    @PostMapping("/read")
    public ViewData isReadAndCompleted(@RequestBody Long[] ids){
        try{
            notifyService.readNotify(ids);
            return ViewData.ok();
        }catch (Exception e){
            e.printStackTrace();
        }
        return ViewData.error("fail");
    }
    @GetMapping("/task")
    //fromUserId指的是原通知的发起者
    public ViewData dealWasIssuedTask(Long notifyId, Long taskId, Long fromUserId, Boolean isAccept){
        try{
            notifyService.dealWasIssuedTask(notifyId, taskId, isAccept, fromUserId);
            return ViewData.ok();
        }catch (Exception e){
            e.printStackTrace();
        }
        return ViewData.error("fail");
    }

}
