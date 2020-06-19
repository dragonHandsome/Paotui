package org.dragon.paotui.service;

import com.github.pagehelper.PageInfo;
import org.dragon.paotui.payload.*;
import org.dragon.paotui.pojo.TaskCertificate;
import org.dragon.paotui.pojo.TaskHandleRecord;
import org.dragon.paotui.pojo.WechatTask;
import org.dragon.paotui.pojo.WechatTaskCancel;

import java.util.List;

public interface TaskService extends BaseService<WechatTask>{
    TaskForm getTaskForm(Long categoryId);

    Long issueTask(TaskForm taskForm, Long userId);

    TaskResp getTasks(WechatTask wechatTask);

    TaskResp getTasks(WechatTask wechatTask, Integer offset);

    TaskResp getTasks(WechatTask wechatTask, Integer offset, Integer limit);

    WechatTask getTaskDetail(Long taskId);

    void updateTask(WechatTask wechatTask);

    void updateTaskStatus(TaskReq taskReq, Long currentUserId);

    void cancelTask(WechatTaskCancel wechatTaskCancel, Long currentUserId);

    void caiNa(CaiNaReq caiNaReq, long userId);

    List<WechatTask> getCompletedTasks();

    void acceptTask(Long taskId);

    void rejectTask(Long taskId);

    PageInfo queryTasks(TaskQuery taskQuery);

    List<WechatTask> findUnfinishedTaskByIntervalDay(Integer intervalDay);

    void toCompleteTask(WechatTask wechatTask);

    void toCancelTask(WechatTask wechatTask);

    void handleTask(TaskHandleRecord taskHandleRecord);

    void cancelTaskCauseToUser(Long taskId, long userId);

    void submitCertification(TaskCertificate taskCertificate, Long curUserId);

    void rejectCancel(Long taskId, long userId);

    void argeeCancel(Long taskId, long userId);
}
