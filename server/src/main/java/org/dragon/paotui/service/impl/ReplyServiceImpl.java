package org.dragon.paotui.service.impl;

import org.dragon.paotui.payload.ReplyReq;
import org.dragon.paotui.payload.ReplyResp;
import org.dragon.paotui.payload.UserDetailResp;
import org.dragon.paotui.pojo.Reply;
import org.dragon.paotui.pojo.WechatUser;
import org.dragon.paotui.service.ReplyService;
import org.dragon.paotui.service.TaskService;
import org.dragon.paotui.service.WechatUserService;
import org.dragon.paotui.utils.NotifyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ReplyServiceImpl extends AbstractService<Reply> implements ReplyService {
    @Autowired
    WechatUserService userService;
    @Autowired
    NotifyFactory notifyFactory;
    @Autowired
    TaskService taskService;
    @Override
    public Reply insertAutoPrimary(Reply model) {
        Reply reply = super.insertAutoPrimary(model);
        return reply;
    }

    @Override
    @CacheEvict(value = "reply", key = "#p0.taskId")
    public void insertReply(ReplyReq replyReq, Long currentId) {
        final Reply build = Reply.builder()
                .createdTime(new Date())
                .content(replyReq.getContent())
                .taskId(replyReq.getTaskId())
                .toReplyId(replyReq.getToReplyId())
                .fromUserId(currentId)
                .toUserId(replyReq.getToUserId())
                .build();
        insertAutoPrimary(build);
        createReplyNotify(replyReq, currentId);
    }

    private void createReplyNotify(ReplyReq replyReq, Long currentId) {
        WechatUser principal = (WechatUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long toUserId = replyReq.getToUserId() != null && replyReq.getToUserId() > 0?
                replyReq.getToUserId() :
                taskService.getTaskDetail(replyReq.getTaskId()).getFromUserId();
        if(currentId == toUserId) return;
        notifyFactory.init(0l, toUserId)
                .createReplyNotify("用户("+principal.getName()+")回复您:" + replyReq.getContent(),replyReq.getTaskId());
    }

    public void updateReply(Reply model) {
        update(model);
    }

    @Override
    //下次直接resultMap算了
    @Cacheable(value = "reply", key = "#p0")
    public ReplyResp getReplyList(Long taskId) {
        final List<Reply> replies = findAllByProperty("taskId", taskId);
        List<ReplyResp.ReplyDetail> replyDetails = new ArrayList<>();
        replies.forEach(reply -> {
            UserDetailResp fromUser = userService.findUserDetail(reply.getFromUserId());
            UserDetailResp toUser = null;
            if(reply.getToUserId() == reply.getFromUserId())
                toUser = fromUser;
            else{
                if(reply.getToUserId() != null && reply.getToUserId() != 0)
                    toUser = userService.findUserDetail(reply.getToUserId());
            }


            final ReplyResp.ReplyDetail build = ReplyResp.ReplyDetail.builder()
                    .id(reply.getId())
                    .content(reply.getContent())
                    .createdTime(reply.getCreatedTime())
                    .taskId(reply.getTaskId())
                    .toReplyId(reply.getToReplyId())
                    .fromUser(fromUser)
                    .toUser(toUser)
                    .build();
            replyDetails.add(build);
        });
        return ReplyResp.builder().replyList(replyDetails).build();
    }
}
