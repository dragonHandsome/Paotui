package org.dragon.paotui.service.Schedule;

import org.dragon.paotui.enumeration.TaskStatus;
import org.dragon.paotui.payload.TaskQuery;
import org.dragon.paotui.pojo.WechatTask;
import org.dragon.paotui.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;

@Component
public class TaskAutoSchedule {
    //2后的task没完成就自动结束
    private static final Integer intervalDay = 2;
    @Autowired
    TaskService taskService;
    public void startTimer(){
        Timer timer = new Timer();
        Calendar instance = Calendar.getInstance();
        instance.set(2020, 4, 11, 6, 00);
        Date time = instance.getTime();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handleTask();
            }
        }, time, 24 * 60 * 60);
    }

    private void handleTask() {
        //拿到超过2天未完成的任务
        List<WechatTask> tasks = taskService.findUnfinishedTaskByIntervalDay(2);
        //任务等待确认则是 需要被完成 否则撤销
        Predicate neadToBeComplete = (status) -> status == TaskStatus.AWAIT_CONFIRM;
        tasks.forEach(wechatTask -> {
            if(neadToBeComplete.test(wechatTask.getStatus())) {
                taskService.toCompleteTask(wechatTask);
            } else {
                taskService.toCancelTask(wechatTask);
            }
        });
    }
}
