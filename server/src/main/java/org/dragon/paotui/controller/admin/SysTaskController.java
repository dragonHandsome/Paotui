package org.dragon.paotui.controller.admin;

import com.github.pagehelper.PageInfo;
import org.dragon.paotui.mapper.StatisticsMapper;
import org.dragon.paotui.payload.Statistics;
import org.dragon.paotui.payload.TaskCancelQuery;
import org.dragon.paotui.payload.TaskQuery;
import org.dragon.paotui.payload.ViewData;
import org.dragon.paotui.pojo.*;
import org.dragon.paotui.security.CurrentUser;
import org.dragon.paotui.service.TaskService;
import org.dragon.paotui.service.WechatTaskCancelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/adminPage/task")
public class SysTaskController {
    @Autowired
    TaskService taskService;
    @Autowired
    WechatTaskCancelService cancelService;

    @GetMapping
    public ViewData getTasks(TaskQuery taskQuery) {
        try{
            PageInfo tasks = taskService.queryTasks(taskQuery);
            return ViewData.ok(tasks);
        }catch (Exception e) {
            e.printStackTrace();
            return ViewData.error(e.getMessage());
        }
    }
    @GetMapping("{taskId}")
    public ViewData getTaskDetail(@PathVariable Long taskId) {
        try{
            WechatTask task = taskService.getTaskDetail(taskId);
            return ViewData.ok(task);
        }catch (Exception e) {
            e.printStackTrace();
            return ViewData.error(e.getMessage());
        }
    }

    @PostMapping
    public ViewData HandelTasks(@RequestBody @Valid TaskHandleRecord taskHandleRecord, @CurrentUser Admin admin) {
        try{
            taskHandleRecord.setAdminId(admin.getId());
            taskService.handleTask(taskHandleRecord);
            return ViewData.ok();
        }catch (Exception e) {
            e.printStackTrace();
            return ViewData.error(e.getMessage());
        }
    }
    @GetMapping("/cancelApplyRecord")
    public ViewData cancelApplyRecord(TaskCancelQuery query) {
        try{
            PageInfo cancels =  cancelService.getCancelApplyRecord(query);
            return ViewData.ok(cancels);
        }catch (Exception e) {
            e.printStackTrace();
            return ViewData.error(e.getMessage());
        }
    }
    @GetMapping("/certification/{taskId}")
    public ViewData getCertification(@PathVariable Long taskId) {
        try{
            TaskCertificate certification =  cancelService.getCertificationByTaskId(taskId);
            return ViewData.ok(certification);
        }catch (Exception e) {
            e.printStackTrace();
            return ViewData.error(e.getMessage());
        }
    }

}
