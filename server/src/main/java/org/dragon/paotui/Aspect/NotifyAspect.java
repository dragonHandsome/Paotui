package org.dragon.paotui.Aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.dragon.paotui.enumeration.Action;
import org.dragon.paotui.enumeration.NotifyType;
import org.dragon.paotui.payload.NotifyContent;
import org.dragon.paotui.payload.TaskForm;
import org.dragon.paotui.payload.UserDetailResp;
import org.dragon.paotui.pojo.Notify;
import org.dragon.paotui.pojo.WechatCategory;
import org.dragon.paotui.service.CategoryService;
import org.dragon.paotui.service.NotifyService;
import org.dragon.paotui.service.WechatUserService;
import org.dragon.paotui.utils.MyLogUtil;
import org.dragon.paotui.utils.NotifyFactory;
import org.dragon.paotui.utils.NotifyServiceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Aspect
@Component
public class NotifyAspect {
    private static long SYSTEM_KEY = 0l;
    @Autowired
    WechatUserService userService;
    @Autowired
    NotifyService notifyService;
    @Autowired
    NotifyFactory notifyFactory;
    @Pointcut("execution(public * org.dragon.paotui.utils.NotifyFactory.create*Notify(..))")
    public void Pointcut(){}
    @Around("Pointcut()")
    @Transactional
    public Object around(ProceedingJoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        Notify notify = null;
            try {
                notify = (Notify) joinPoint.proceed(args);
                //系统通知 发给所有
                if(notify.getFromUserId() == SYSTEM_KEY && notify.getToUserId() == SYSTEM_KEY){
                    NotifyWebsocket.sendNotifyToAll(notify);
                } else {
                    notifyService.insertAutoPrimary(notify);
                    //不是发给系统的就直接发
                    if(notify.getToUserId() != SYSTEM_KEY){
                        NotifyWebsocket.sendNotify(notify, notify.getToUserId());
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
                MyLogUtil.error("user:" +notify.getFromUser()+"对"+notify.getToUserId()+"发布任务失败。");
            } finally {
                notifyFactory.remove();
            }
        return notify;
    }

}
