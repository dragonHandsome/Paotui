package org.dragon.paotui.controller;

import org.dragon.paotui.payload.ReplyReq;
import org.dragon.paotui.payload.ReplyResp;
import org.dragon.paotui.payload.ViewData;
import org.dragon.paotui.pojo.WechatUser;
import org.dragon.paotui.security.CurrentUser;
import org.dragon.paotui.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("reply")
public class ReplayController {
    @Autowired
    ReplyService replyService;
    @PutMapping
    public ViewData<?> reply(@RequestBody @Valid ReplyReq replyReq, @CurrentUser WechatUser user){
        try{
            replyService.insertReply(replyReq, user.getId());
            return ViewData.ok();
        }catch (Exception e){
            e.printStackTrace();
        }
        return ViewData.error("");
    }
    @GetMapping
    public ViewData<?> getReplyList(Long taskId, @CurrentUser WechatUser user){
        try{
            ReplyResp resp =  replyService.getReplyList(taskId);
            return ViewData.ok(resp);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ViewData.error("");
    }
}
