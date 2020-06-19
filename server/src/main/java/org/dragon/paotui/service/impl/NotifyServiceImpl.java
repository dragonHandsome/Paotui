package org.dragon.paotui.service.impl;

import org.dragon.paotui.Aspect.NotifyUser;
import org.dragon.paotui.mapper.NotifyMapper;
import org.dragon.paotui.pojo.Notify;
import org.dragon.paotui.pojo.WechatUser;
import org.dragon.paotui.service.NotifyService;
import org.dragon.paotui.service.TaskService;
import org.dragon.paotui.service.WechatUserService;
import org.dragon.paotui.utils.NotifyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class NotifyServiceImpl extends AbstractService<Notify> implements NotifyService {
    @Autowired
    TaskService taskService;
    @Autowired
    WechatUserService userService;
    @Autowired
    NotifyFactory notifyFactory;
    @Autowired
    NotifyMapper mapper;
    @Override
    public void isRead(Long notifyId) {
        //通知不能被处理过
        //requireNotRead(notifyId);
        Notify build = Notify.builder()
                .id(notifyId)
                .isRead(true)
                .build();
        update(build);
    }

    public void requireNotRead(Long notifyId){
        Notify notify = findById(notifyId);
        Assert.isTrue(notify.getIsRead() == false, "通知已经被处理");
    }

    @Override
    @Transactional
    public void dealWasIssuedTask(Long notifyId, Long taskId, Boolean isAccept, Long fromUserId) {
        //如果接收任务

        if(isAccept){
            taskService.acceptTask(taskId);
        }
        //拒绝接受任务
        else {
            taskService.rejectTask(taskId);
        }
        isRead(notifyId);

    }

    @Override
    public List<Notify> getNotReadNotifies(Long userId) {
        Notify condition = Notify.builder()
                .isRead(false)
                .toUserId(userId)
                .build();
        List<Notify> notifies = findByEntity(condition);
        takeUserDetail(notifies);
        return notifies;
    }

    @Override
    public void readNotify(Long[] ids) {
        mapper.readNotifies(ids);
    }

    @Override
    public List<Notify> getNotReadNotifies(Long toUserId, Long fromUserId) {
        Notify condition = Notify.builder()
                .isRead(false)
                .toUserId(toUserId)
                .fromUserId(fromUserId)
                .build();
        List<Notify> notifies = findByEntity(condition);
        takeUserDetail(notifies);
        return notifies;
    }

    private void takeUserDetail(List<Notify> notifies) {
        notifies.forEach(notify -> {
            if(notify.getFromUserId() > 0l){
                notify.setFromUser(
                        NotifyUser.getInstance(notify.getFromUserId())
                );
            }
            if(notify.getToUserId() > 0l) {
                notify.setToUser(
                        NotifyUser.getInstance(notify.getToUserId())
                );
            }
        });
    }

}
