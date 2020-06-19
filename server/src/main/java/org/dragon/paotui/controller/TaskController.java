package org.dragon.paotui.controller;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.dragon.paotui.enumeration.ErrorResp;
import org.dragon.paotui.enumeration.TaskStatus;
import org.dragon.paotui.payload.*;
import org.dragon.paotui.pojo.*;
import org.dragon.paotui.security.CurrentUser;
import org.dragon.paotui.service.CategoryService;
import org.dragon.paotui.service.TaskService;
import org.dragon.paotui.service.WechatUserService;
import org.dragon.paotui.utils.MyLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/task")
@Slf4j
public class TaskController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    TaskService taskService;
    @Autowired
    WechatUserService wechatUserService;


    @RequestMapping(method = RequestMethod.GET)
    public ViewData<?> getTask(WechatTask wechatTask,Integer page, Integer limit,Boolean special, @CurrentUser WechatUser wechatUser){
        long currentUserId = wechatUser.getId();
        try{
            TaskResp taskResp = taskService.getTasks( wechatTask, page, limit);
            validateTaskAuthority(currentUserId, taskResp, special);
            if(taskResp.getTasks().size() ==  0){
                return ViewData.error(ErrorResp.NOT_MORE);
            }
            return ViewData.ok(taskResp);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ViewData.error(ErrorResp.GET_INFO_ERROR);
    }
    //这里简单过滤下 可以直接改变数据库查询条件更快
    private void validateTaskAuthority(long currentUserId, TaskResp taskResp, Boolean special) {
        //如果不是用户新发布的任务 则只能用户自己或者接单用户看
        List<TaskResp.TaskDetail> collect = taskResp.getTasks().stream().filter(taskDetail -> {
            WechatTask task = taskDetail.getTask();
            Boolean condition = task.getStatus() == TaskStatus.AWAIT_TAKEN ||
                    task.getFromUserId() == currentUserId ||
                    task.getToUserId() == currentUserId;
            condition = condition &&( (task.getStatus() != TaskStatus.AWAIT_TAKEN || special == null || special == false) || task.getToUserId() == null);
            return condition;
        }).collect(Collectors.toList());
        taskResp.setTasks(collect);
    }

    @RequestMapping(value = "/wantCancel", method = RequestMethod.POST)
    public ViewData<?> wantCancelTask(@RequestBody @Valid WechatTaskCancel wechatTaskCancel, @CurrentUser WechatUser curUser){
        try{
            long userId = curUser.getId();
            taskService.cancelTask(wechatTaskCancel, userId);
            return ViewData.ok();
        }catch (Exception e){
            e.printStackTrace();
        }
        return ViewData.error(ErrorResp.UPDATE_ERROR);
    }

    @GetMapping(value = "/reject")
    public ViewData<?> rejectCancel(Long taskId, @CurrentUser WechatUser curUser){
        try{
            long userId = curUser.getId();
            taskService.rejectCancel(taskId, userId);
            return ViewData.ok();
        }catch (Exception e){
            e.printStackTrace();
        }
        return ViewData.error(ErrorResp.UPDATE_ERROR);
    }

    @GetMapping(value = "/agree")
    public ViewData<?> agreeCancel(Long taskId, @CurrentUser WechatUser curUser){
        try{
            long userId = curUser.getId();
            taskService.argeeCancel(taskId, userId);
            return ViewData.ok();
        }catch (Exception e){
            e.printStackTrace();
        }
        return ViewData.error(ErrorResp.UPDATE_ERROR);
    }

    @RequestMapping(value = "/cert", method = RequestMethod.POST)
    public ViewData<?> certTask(@RequestBody TaskCertificate taskCertificate, @CurrentUser WechatUser curUser){
        try{
            Long curUserId = curUser.getId();
            taskService.submitCertification(taskCertificate, curUserId);
            return ViewData.ok();
        }catch (Exception e){
            e.printStackTrace();
        }
        return ViewData.error(ErrorResp.UPDATE_ERROR);
    }



    //取消惩罚
    @RequestMapping(value = "/cancelBad", method = RequestMethod.GET)
    public ViewData<?> cancelTask(Long taskId, @CurrentUser WechatUser curUser){
        try{
            long userId = curUser.getId();
            taskService.cancelTaskCauseToUser(taskId, userId);
            return ViewData.ok();
        }catch (Exception e){
            e.printStackTrace();
        }
        return ViewData.error(ErrorResp.UPDATE_ERROR);
    }

    @RequestMapping(value = "/status", method = RequestMethod.POST)
    public ViewData<?> updateTaskStatus(@RequestBody TaskReq taskReq, @CurrentUser WechatUser curUser){
        try{
            long userId = curUser.getId();
            taskService.updateTaskStatus(taskReq, userId);
            return ViewData.ok();
        }catch (Exception e){
            e.printStackTrace();
        }
        return ViewData.error(ErrorResp.UPDATE_ERROR);
    }

    @RequestMapping(value = "/caina", method = RequestMethod.POST)
    @CacheEvict(value = "reply", key = "#p0.taskId")
    public ViewData<?> caiNa(@RequestBody @Valid CaiNaReq caiNaReq, @CurrentUser WechatUser curUser){
        try{
            long userId = curUser.getId();
            taskService.caiNa(caiNaReq, userId);
            return ViewData.ok();
        }catch (Exception e){
            e.printStackTrace();
        }
        return ViewData.error(ErrorResp.UPDATE_ERROR);
    }
    @RequestMapping(method = RequestMethod.POST)
    public ViewData<?> updateTask(@RequestBody TaskReq taskReq, @CurrentUser WechatUser curUser){
        try{
            long userId = curUser.getId();
            WechatTask wechatTask = generateWechatTask(taskReq, userId);
            taskService.updateTask(wechatTask);
            return ViewData.ok();
        }catch (Exception e){
            e.printStackTrace();
            return ViewData.error(e.getMessage());
        }
    }

    private WechatTask generateWechatTask(TaskReq taskReq, Long userId) {
        WechatTask task = taskService.findById(taskReq.getId());
        //需本人
        Assert.isTrue(task.getFromUserId() == userId,"需本人更改自己的任务信息");
        return WechatTask.builder()
                .id(task.getId())
                .title(taskReq.getTitle())
                .rewardMoney(taskReq.getRewardMoney())
                .lastModifiedTime(new Date())
                .content(taskReq.getContent())
                .star(taskReq.getStar())
                .wechatOptions(taskReq.getWechatOptions())
                .build();
    }


    @RequestMapping(value="/detail",method = RequestMethod.GET)
    public ViewData<?> getTaskDetail(Long taskId){
        try{
            WechatTask wechatTasks = taskService.getTaskDetail(taskId);
            return ViewData.ok(wechatTasks);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ViewData.error(ErrorResp.GET_INFO_ERROR);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ViewData<?> issueTask(@RequestBody @Valid TaskForm taskForm, @CurrentUser WechatUser curUser){
        long userId = curUser.getId();
        try{
            validateAndChangeForm(taskForm);
            taskService.issueTask(taskForm, userId);
            return ViewData.ok();
        }catch (Exception e){
            e.printStackTrace();
            return ViewData.error(e.getMessage());
        }
    }

    private void validateAndChangeForm(TaskForm taskForm) {
        List<WechatOption> collect = taskForm.getWechatOptions().stream().filter(wechatOption -> {
            return StringUtils.hasText(wechatOption.getValue());
        }).collect(Collectors.toList());
        taskForm.setWechatOptions(collect);
    }

    @RequestMapping(value = "/category", method = RequestMethod.GET)
    public ViewData<?> getCategory(){
        List<WechatCategory> categories = categoryService.selectAll();
        if(categories != null && categories.size() > 0){
            return ViewData.ok(categories);
        }
        return ViewData.error(ErrorResp.GET_INFO_ERROR);
    }

    @RequestMapping(value ="/form", method = RequestMethod.GET)
    public ViewData<?> getTaskForm(Long categoryId){
        try{
            TaskForm taskForm = taskService.getTaskForm(categoryId);
            return ViewData.ok(taskForm);
        }catch (Exception e){

        }
        return ViewData.error(ErrorResp.GET_INFO_ERROR);
    }
}
