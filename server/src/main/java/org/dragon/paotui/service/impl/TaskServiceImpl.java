package org.dragon.paotui.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.dragon.paotui.enumeration.CategoryType;
import org.dragon.paotui.enumeration.TaskDealStatus;
import org.dragon.paotui.enumeration.TaskStatus;
import org.dragon.paotui.mapper.TaskCertificateMapper;
import org.dragon.paotui.mapper.TaskHandleRecordMapper;
import org.dragon.paotui.mapper.WechatTaskMapper;
import org.dragon.paotui.payload.*;
import org.dragon.paotui.pojo.*;
import org.dragon.paotui.service.*;
import org.dragon.paotui.utils.MyLogUtil;
import org.dragon.paotui.utils.NotifyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.function.Predicate;

@Service
public class TaskServiceImpl extends AbstractService<WechatTask>  implements TaskService {

    @Autowired
    CategoryService categoryService;
    @Autowired
    WechatTaskMapper wechatTaskMapper;
    @Autowired
    WechatUserService wechatUserService;
    @Autowired
    AccountService accountService;
    @Autowired
    ReplyService replyService;
    @Autowired
    TaskHandleRecordMapper taskHandleRecordMapper;
    @Autowired
    TaskCertificateMapper taskCertificateMapper;
    @Override
    @Cacheable(value = "taskForm",key = "#p0")
    public TaskForm getTaskForm(Long categoryId) {
        List<WechatOption> wechatOptions =
                categoryService.findOptionsByCategoryId(categoryId);
        TaskForm build = TaskForm.builder()
                .wechatOptions(wechatOptions)
                .categoryId(categoryId)
                .build();
        return build;
    }

    @Override
    @Transactional
    public Long issueTask(TaskForm taskForm, Long userId) {
        //这里先不验证toUserId是否正确
        WechatTask build = WechatTask.builder()
                .categoryId(taskForm.getCategoryId())
                .content(taskForm.getContent())
                .fromUserId(userId)
                .toUserId(taskForm.getToUserId())
                .createTime(new Date())
                .lastModifiedTime(new Date())
                .rewardMoney(taskForm.getRewardMoney())
                .title(taskForm.getTitle())
                .status(TaskStatus.AWAIT_TAKEN)
                .build();
        if(taskForm.getRewardMoney() != null && taskForm.getRewardMoney() > 0){
            accountService.transferSystemNative(userId, taskForm.getRewardMoney().intValue());

        }wechatTaskMapper.insertSelective(build);

        Long taskId = wechatTaskMapper.lastInsertPrimary();
        Objects.requireNonNull(taskId);
        if(taskForm.getWechatOptions().size() > 0){
            wechatTaskMapper.insertTaskOptions(taskForm.getWechatOptions(), taskId);
        }
        sendNotify(taskForm, userId, taskId);
        return taskId;
    }
    @Autowired
    NotifyFactory notifyFactory;
    private void sendNotify(TaskForm taskForm, Long userId, Long taskId) {
        if(taskForm.getToUserId() != null && taskForm.getToUserId() > 0){
            NotifyFactory instance = notifyFactory.init(userId, taskForm.getToUserId());
            instance.createWasIssuedNotify(taskId, taskForm.getTitle());
        }
    }

    private Integer PAGE_DEFAULT = 0;
    private Integer LIMIT_DEFAULT = 4;
    @Override
    public TaskResp getTasks(WechatTask wechatTask) {
        return getTasks(wechatTask, PAGE_DEFAULT);
    }

    @Override
    public TaskResp getTasks(WechatTask wechatTask, Integer page) {
        if(page == null) page = PAGE_DEFAULT;
        return getTasks(wechatTask, page, LIMIT_DEFAULT);
    }

    @Override
    @Transactional
    public TaskResp getTasks(WechatTask wechatTask, Integer page, Integer limit) {
//        Example example = new Example(WechatTask.class);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo(wechatTask);
        TaskQuery query = TaskQuery.builder()
                .categoryId(wechatTask.getCategoryId())
                .page(page)
                .limit(limit)
                .status(wechatTask.getStatus())
                .title(wechatTask.getTitle())
                .sort("-id")
                .fromUserId(wechatTask.getFromUserId())
                .toUserId(wechatTask.getToUserId())
                .build();
        Example example = createQueryExample(query);
        //example.orderBy("createTime").desc();
        if(page == null) page = PAGE_DEFAULT;
        if(limit == null) limit = LIMIT_DEFAULT;
        PageHelper.startPage(page, limit);
        List<WechatTask> wechatTasks = wechatTaskMapper.selectByExample(example);
        List<TaskResp.TaskDetail> taskDetails = new ArrayList<>();
        wechatTasks.forEach(task -> {
            WechatUser user = wechatUserService.findWechatUserByUserId(task.getFromUserId());
            WechatUserDetail userDetail = wechatUserService.findWechatUserDetailByUserId(task.getFromUserId());
            TaskResp.TaskDetail build = TaskResp.TaskDetail.builder()
                    .fromUser(user)
                    .userDetail(userDetail)
                    .task(task)
                    .build();
                taskDetails.add(build);
        });
        List<WechatCategory> wechatCategories = categoryService.selectAll();
        TaskResp build = TaskResp.builder()
                .categories(wechatCategories)
                .tasks(taskDetails)
                .build();
        return build;
    }

    @Override
    public WechatTask getTaskDetail(Long taskId) {
        //WechatTask wechatTask = wechatTaskMapper.findById(taskId);
        WechatTask wechatTask = findById(taskId);
        wechatTask.setCategory(categoryService.findById(wechatTask.getCategoryId()));
        Set<WechatOption> wechatOptions = wechatTaskMapper.getTaskOptions(taskId);
        wechatTask.setWechatOptions(wechatOptions);
        return wechatTask;
    }

    @Override
    @Transactional
    @CacheEvict(value = "taskDetail",key = "#wechatTask.id")
    public void updateTask(WechatTask wechatTask) {
        update(wechatTask);
        if(wechatTask.getWechatOptions()!=null){
            wechatTask.getWechatOptions().forEach(wechatOption-> {
                if (validateWechatOption(wechatOption))
                    wechatTaskMapper.updateOptions(wechatOption, wechatTask.getId());
            });
        }
    }

    @Override
    @Cacheable(value = "taskDetail",key = "#p0")
    public WechatTask findById(Long id) {
        return super.findById(id);
    }



    @Override
    @CacheEvict(value = "taskDetail",key = "#p0.id")
    public void updateTaskStatus(TaskReq taskReq, Long currentUserId) {
        WechatTask task = findById(taskReq.getId());
        //如果请求的状态为空则抛出异常
        if(taskReq.getStatus() == null) throw new RuntimeException("请求更新的状态为空");
        //验证状态
        try{
            validateStatus(task, taskReq, currentUserId);
        }catch (Exception e){
            MyLogUtil.throwError("恶意更改订单状态");
        }
        //更新订单状态
        updateTaskStatusNormal(task, taskReq, currentUserId);
        //完成给接单用户打钱
        if(taskReq.getStatus() == TaskStatus.COMPLETE){
            accountService.transferUserNative(task.getToUserId(), task.getRewardMoney().intValue());
        }
        createTaskUpdateNotify(task, taskReq, currentUserId);
    }

    private void createTaskUpdateNotify(WechatTask task, TaskReq taskReq, Long currentUserId) {
        WechatUser curUser = (WechatUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        switch (taskReq.getStatus()){
            case AWAIT_COMMIT://告知发任务者我接收对方任务
            {

                NotifyFactory factory = notifyFactory.init(currentUserId, task.getFromUserId());
                factory.createTaskTakenNotify(task.getId());
                //系统告诉接收任务成功
                factory = notifyFactory.init(0l, currentUserId);
                factory.createTaskNotify("成功接收任务，快去帮TA完成吧~" , task.getId());
            }break;
            case AWAIT_CONFIRM:{//告知对方我提交任务
                NotifyFactory factory = notifyFactory.init(currentUserId, task.getFromUserId());
                factory.createTaskCommitNotify(task.getId());
            }break;
            case COMPLETE://用户确认完成任务 系统告知接单者
            {
                NotifyFactory factory = notifyFactory.init(0l, task.getToUserId());
                factory.createTaskConfirmedNotify(task.getId(), task.getRewardMoney());
            }break;
            case CANCELED:{
                //告知发单用户取消成功
                NotifyFactory factory = notifyFactory.init(0l, task.getFromUserId());
                factory.createTaskCanceledNotify(task.getId());
                //告知接单用户取消成功
                factory = notifyFactory.init(0l, task.getToUserId());
                factory.createTaskCanceledNotify(task.getId());
            }


        }
    }

    @Autowired
    WechatTaskCancelService cancelService;

    Predicate noInReqCancel = status -> status != TaskStatus.REQ_CANCEL;
    @Override
    //申请取消
    @Transactional
    @CacheEvict(value = "taskDetail",key = "#p0.taskId")
    public void cancelTask(WechatTaskCancel wechatTaskCancel, Long currentUserId) {
        WechatTask task = findById(wechatTaskCancel.getTaskId());
        if(task.getFromUserId() == currentUserId &&
                noInReqCancel.and(noFinishedTask).test(task.getStatus())
        ) {
            task.setStatus(TaskStatus.REQ_CANCEL);
            task.setLastModifiedTime(new Date());
            update(task);

           wechatTaskCancel.setStatus(TaskDealStatus.PENDING);
           wechatTaskCancel.setCreateTime(new Date());
            wechatTaskCancel.setLastModifiedTime(new Date());
           cancelService.insertAutoPrimary(wechatTaskCancel);

           //发通知
            notifyFactory.init(0l, task.getToUserId());
            notifyFactory.createTaskNotify("用户申请取消此次任务,理由:" +
                    wechatTaskCancel.getContent()
            , task.getId()
            );
        }
    }

    @Override
    @CacheEvict(value = "taskDetail",key = "#p0.id")
    public void update(WechatTask model) {
        super.update(model);
    }

    @Override
    @Transactional
    @CacheEvict(value = "taskDetail",key = "#p0.taskId")
    public void caiNa(CaiNaReq caiNaReq, long curUserId) {
        Objects.requireNonNull(caiNaReq.getTaskId());
        Objects.requireNonNull(caiNaReq.getCaiNaUserId());
        WechatTask task = findById(caiNaReq.getTaskId());
        Assert.isTrue(curUserId == task.getFromUserId(), "对自己的task操作");
        Assert.isTrue(task.getStatus() == TaskStatus.AWAIT_CONFIRM || task.getStatus() == TaskStatus.AWAIT_COMMIT || task.getStatus() == TaskStatus.AWAIT_TAKEN, "新发布的问题任务才能采纳");
        //生成更新任务所需数据
        WechatTask build = WechatTask.builder()
                .id(task.getId())
                .toUserId(caiNaReq.getCaiNaUserId())
                //设置任务状态为已完成
                .status(TaskStatus.COMPLETE)
                .lastModifiedTime(new Date())
                //采纳默认给5星
                .star(5)
                .build();
        //更新任务
        update(build);
        //更新reply
        Reply build1 = Reply.builder()
                .id(caiNaReq.getReplyId())
                .toUserId(curUserId)
                .build();
        replyService.updateReply(build1);
        //给与报酬
        accountService.transferUserNative(caiNaReq.getCaiNaUserId(), task.getRewardMoney().intValue());
        createCaiNaNotify(task.getFromUserId(), caiNaReq.getCaiNaUserId(), caiNaReq.getTaskId(), task.getRewardMoney().intValue());
    }

    private void createCaiNaNotify(Long fromUserId, Long caiNaUserId, Long taskId, Integer money) {
        NotifyFactory init = notifyFactory.init(fromUserId, caiNaUserId);
        init.createCaiNaNotify(taskId, money);
    }

    @Override
    public List<WechatTask> getCompletedTasks() {
        WechatTask condition = WechatTask.builder()
                .status(TaskStatus.COMPLETE)
                .build();
        return findByEntity(condition);
    }

    @Override
    public void acceptTask(Long taskId) {
        WechatTask task = isWasIssueTask(taskId);
        WechatTask build = WechatTask.builder()
                .id(taskId)
                .status(TaskStatus.AWAIT_COMMIT)
                .build();
        update(build);
        //告诉发任务的 我接收了
        NotifyFactory factory = notifyFactory.init(task.getToUserId(), task.getFromUserId());
        factory.createTaskTakenNotify(taskId);
    }
    private WechatTask isWasIssueTask(Long taskId) {
        WechatTask task = findById(taskId);
        try{
            Assert.isTrue(task.getStatus() == TaskStatus.AWAIT_TAKEN,"");
        }catch (Exception e){
            MyLogUtil.error("不要重复提交确认！");
        }
        return task;
    }

    @Override
    public void rejectTask(Long taskId) {
        WechatTask task = isWasIssueTask(taskId);
        WechatTask build = WechatTask.builder()
                .id(taskId)
                .status(TaskStatus.CANCELED)
                .build();
        update(build);
        //系统告诉发任务的 用户拒绝了
        NotifyFactory factory = notifyFactory.init(task.getToUserId(), task.getFromUserId());
        factory.createTaskCanceledNotify(taskId);
    }

    @Override
    public PageInfo queryTasks(TaskQuery taskQuery) {
        Example queryExample = createQueryExample(taskQuery);
        PageHelper.startPage(taskQuery.getPage(), taskQuery.getLimit());
        List<WechatTask> wechatTasks = wechatTaskMapper.selectByExample(queryExample);
        for (WechatTask wechatTask : wechatTasks) {
            wechatTask.setCategory(categoryService.findById(wechatTask.getCategoryId()));
        }
        PageInfo<WechatTask> of = PageInfo.of(
                wechatTasks
        );
        return of;
    }

    //找到需要处理的task task 5天内自动处理取消或者完成
    @Override
    public List<WechatTask> findUnfinishedTaskByIntervalDay(Integer intervalDay) {
        //new Date()优化下sql 不用每次now()
        List<WechatTask> unfinishedTaskByIntervalDay =
                wechatTaskMapper.findUnfinishedTaskByIntervalDay(new Date(), intervalDay);
        return unfinishedTaskByIntervalDay;
    }

    @Override
    @Transactional
    public void toCompleteTask(WechatTask wechatTask) {
        wechatTask.setStatus(TaskStatus.COMPLETE);
        wechatTask.setLastModifiedTime(new Date());
        update(wechatTask);
        //发奖励
        accountService.transferUserNative(wechatTask.getToUserId(), wechatTask.getRewardMoney().intValue());

    }

    @Override
    @Transactional
    public void toCancelTask(WechatTask wechatTask) {
        wechatTask.setStatus(TaskStatus.CANCELED);
        wechatTask.setLastModifiedTime(new Date());
        update(wechatTask);
        //退钱
        accountService.transferUserNative(wechatTask.getFromUserId(), wechatTask.getRewardMoney().intValue());
        if(wechatTask.getToUserId() != null) {
            notifyFactory.init(0l, wechatTask.getToUserId());
            notifyFactory.createTaskCanceledNotify(wechatTask.getId());
        }
        notifyFactory.init(0l, wechatTask.getFromUserId());
        notifyFactory.createTaskCanceledNotify(wechatTask.getId());
    }
    @Override
    @Transactional
    //管理员处理
    public void handleTask(TaskHandleRecord taskHandleRecord) {
        switch (taskHandleRecord.getType()) {
            // 退钱
            case illegal_cancel: {
                refundTask(taskHandleRecord.getTaskId());
                illegalTaskPunishment(taskHandleRecord);
            }break;
            case refund: {
                WechatTask task = findById(taskHandleRecord.getTaskId());
                if(task.getStatus() == TaskStatus.REQ_CANCEL) {
                    refundTask(taskHandleRecord.getTaskId());
                }else {
                    MyLogUtil.error("任务未请求取消");
                }
                completeCancelTask(task.getId());
                notifyFactory.init(0l, task.getToUserId());
                notifyFactory.createWhyAgreeCancelNotify(taskHandleRecord.getReason(),task.getId());
            }break;
            default: MyLogUtil.error("未做任何操作");
        }
        taskHandleRecordMapper.insertSelective(taskHandleRecord);
    }

    private void completeCancelTask(Long taskId) {
        WechatTaskCancel condition = WechatTaskCancel.builder()
                .taskId(taskId).build();
        WechatTaskCancel build = WechatTaskCancel.builder()
                .lastModifiedTime(new Date())
                .status(TaskDealStatus.COMPLETE)
                .build();
        cancelService.updateByEntity(build, condition);
    }

    private void illegalTaskPunishment(TaskHandleRecord taskHandleRecord) {
        Integer reduce = 200;
        WechatTask task = findById(taskHandleRecord.getTaskId());
        wechatUserService.reduceHiddenPoint(taskHandleRecord.getTaskId(), reduce);
        notifyFactory.init(0l, task.getFromUserId());
        notifyFactory.createIllegalNotify(taskHandleRecord.getReason(), task.getId());
    }

    @Override
    @Transactional
    public void cancelTaskCauseToUser(Long taskId, long userId) {
        WechatTask task = getTaskDetail(taskId);
        //验证未完成的任务 以及任务的接收者是当前用户 才能作出如此举动
        if(noFinishedTask.test(task.getStatus()) && task.getToUserId() == userId) {
            //恢复task为等待接收状态
            resetTask(task);
            //作出惩罚
            punish(userId);
            //给发单用户通知
            notifyFactory.init(userId, task.getFromUserId());
            notifyFactory.createTaskNotify("接单者放弃了你的任务，等待新的接单者吧~", taskId);
            //给接单用户通知
            notifyFactory.init(0l, userId);
            notifyFactory.createTaskNotify("你已成功取消任务，取消任务将影响您的个人信誉等。", taskId);
        }else {
            MyLogUtil.error("重复操作，或非法请求");
        }
    }

    @Override
    @Transactional
    public void submitCertification(TaskCertificate taskCertificate, Long curUserId) {
        WechatTask task = findById(taskCertificate.getTaskId());
        if(task != null && task.getToUserId() == curUserId &&
            task.getStatus() == TaskStatus.AWAIT_COMMIT
        ) {
            //提交任务
                task.setStatus(TaskStatus.AWAIT_CONFIRM);
                task.setLastModifiedTime(new Date());
                update(task);
            //添加证明
            taskCertificate.setCreateTime(new Date());
            taskCertificateMapper.insert(taskCertificate);
            //通知发单确认
            notifyFactory.init(task.getToUserId(), task.getFromUserId());
            notifyFactory.createTaskCommitNotify(task.getId());
        }
        else MyLogUtil.error("异常提交");
    }

    @Override
    public void rejectCancel(Long taskId, long userId) {
        WechatTaskCancel taskCancel = cancelService.findBy("taskId", taskId);
        if(taskCancel.getStatus() == TaskDealStatus.PENDING) {
            //把 任务发起人的取消申请升级为紧急处理
            taskCancel.setStatus(TaskDealStatus.QUICK_DEAL);
            taskCancel.setLastModifiedTime(new Date());
            cancelService.update(taskCancel);
        }
    }

    @Override
    public void argeeCancel(Long taskId, long userId) {
        WechatTaskCancel taskCancel = cancelService.findBy("taskId", taskId);
        if(taskCancel != null && taskCancel.getStatus() == TaskDealStatus.PENDING ||
                taskCancel.getStatus() == TaskDealStatus.QUICK_DEAL
        ) {
            taskCancel.setStatus(TaskDealStatus.COMPLETE);
            taskCancel.setLastModifiedTime(new Date());
            cancelService.update(taskCancel);
            //取消task
            WechatTask task = findById(taskId);
            toCancelTask(task);
        }else MyLogUtil.error("异常");
    }

    private void punish(long userId) {
        wechatUserService.reduceHiddenPoint(userId);
    }

    //恢复task为等待接收状态
    private void resetTask(WechatTask task) {
        task.setStatus(TaskStatus.AWAIT_TAKEN);
        task.setToUserId(null);
        task.setToUserName(null);
        task.setLastModifiedTime(new Date());
        wechatTaskMapper.updateByPrimaryKey(task);
    }

    Predicate noFinishedTask = (status) -> status != TaskStatus.CANCELED && status != TaskStatus.COMPLETE;
    private void refundTask(Long taskId) {
        WechatTask task = findById(taskId);
        //如果任务未完成 才退钱 防止多次退
        if (noFinishedTask.test(task.getStatus())){
            toCancelTask(task);
        }else MyLogUtil.error("已经操作过");
    }

    private Example createQueryExample(TaskQuery taskQuery) {
        Example example = new Example(WechatTask.class);
        Example.Criteria criteria = example.createCriteria();
        validateTaskQuery(taskQuery);
        if(taskQuery.getCategoryId() != null) {
            criteria.andEqualTo("categoryId", taskQuery.getCategoryId());
        }
        if(taskQuery.getStar() != null) {
            criteria.andEqualTo("star", taskQuery.getStar());
        }
        if(taskQuery.getStatus() != null) {
            criteria.andEqualTo("status", taskQuery.getStatus());
        }
        if(taskQuery.getFromUserId() != null) {
            criteria.andEqualTo("fromUserId", taskQuery.getFromUserId());
        }
        if(taskQuery.getToUserId() != null) {
            criteria.andEqualTo("toUserId", taskQuery.getToUserId());
        }
        if(StringUtils.hasText(taskQuery.getTitle())) {
            criteria.andLike("title", "%" + taskQuery.getTitle() + "%");
        }
        switch (taskQuery.getSort()) {
            case "-id":
                example.orderBy("id").desc();
                break;
        }
        return example;
    }

    private void validateTaskQuery(TaskQuery taskQuery) {
        if (taskQuery.getLimit() == null || taskQuery.getLimit() >= 50)
                taskQuery.setLimit(10);
        if(taskQuery.getPage() == null) taskQuery.setPage(1);
    }

    private void updateTaskStatusNormal(WechatTask task, TaskReq taskReq, Long currentUserId) {
        WechatUser curUser = (WechatUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        WechatTask build = WechatTask.builder()
                .id(task.getId())
                .status(taskReq.getStatus())
                .toUserId(curUser.getId())
                .toUserName(curUser.getName())
                .lastModifiedTime(new Date())
                .build();
        //自己不改toUser信息
        if(currentUserId == task.getFromUserId()){
            build.setToUserId(null);
            build.setToUserName(null);
        }
        //更新状态
        update(build);
    }
    //验证前端传的状态是否合法 不合法抛出和 AssertionError
    @SuppressWarnings("deprecation")
    private void validateStatus(WechatTask task, TaskReq taskReq, Long currentUserId) {
        if(currentUserId != task.getFromUserId()){
//            //是问题
            WechatCategory category = categoryService.findById(task.getCategoryId());
            if(category.getType() == CategoryType.question){
               Assert.isTrue(false, "问题类不通过该验证");
            }
        }
        //如果是对自己的任务操作
        if(currentUserId == task.getFromUserId()){
            switch (task.getStatus()){
                //当任务等待接收 允许直接取消
                case AWAIT_TAKEN:
                    Assert.isTrue(taskReq.getStatus() == TaskStatus.CANCELED);
                    break;
                //当任务等待提交获取等待确认 可确认
                case AWAIT_COMMIT:case AWAIT_CONFIRM:
                    Assert.isTrue(taskReq.getStatus() == TaskStatus.COMPLETE);
                    break;
                default:
                    Assert.isTrue(false);
            }
        }
        //任务未接收任意人可接 任务接收后只有接单人可操作
        else if(task.getToUserId() == null ||
        task.getToUserId() == currentUserId
        ){
            switch (task.getStatus()){
                case AWAIT_TAKEN:
                    Assert.isTrue(taskReq.getStatus() == TaskStatus.AWAIT_COMMIT);
                    break;
                //当任务等待提交获取等待确认 可确认
                case AWAIT_COMMIT:
                    Assert.isTrue(taskReq.getStatus() == TaskStatus.AWAIT_CONFIRM);
                    break;
                default:
                    Assert.isTrue(false);
            }
        }else Assert.isTrue(false);
    }


    private Boolean validateWechatOption(WechatOption wechatOption) {
        return wechatOption.getId()!= null && wechatOption.getValue()!= null;
    }

}
