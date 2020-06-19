package org.dragon.paotui.service;

import org.dragon.paotui.pojo.Notify;

import java.util.List;

public interface NotifyService extends BaseService<Notify>{
    void isRead(Long notifyId);

    void dealWasIssuedTask(Long notifyId, Long taskId, Boolean isAccept, Long fromUserId);

    List<Notify> getNotReadNotifies(Long userId);

    void readNotify(Long[] ids);

    List<Notify> getNotReadNotifies(Long toUserId, Long fromUserId);
}
