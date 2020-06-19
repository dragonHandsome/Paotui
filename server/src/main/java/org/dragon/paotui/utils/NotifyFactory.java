package org.dragon.paotui.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dragon.paotui.Aspect.NotifyUser;
import org.dragon.paotui.Aspect.UserInfo;
import org.dragon.paotui.enumeration.Action;
import org.dragon.paotui.enumeration.NotifyType;
import org.dragon.paotui.payload.NotifyContent;
import org.dragon.paotui.payload.UserDetailResp;
import org.dragon.paotui.pojo.Notify;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
@Component
@Data
public class NotifyFactory {
    private static ThreadLocal<Env> envs = new ThreadLocal();

    public Notify createTaskCommitNotify(Long taskId){
        String msg = "您好，我完成了您的任务，请尽快提交吧~";
        return createBaseNotifyBuilder()
                .type(NotifyType.TASK)
                .content(getContent(msg, taskId))
                .action(Action.task_commit).build();
    }

    public Notify createCaiNaNotify(Long taskId, Integer money) {
        Env env = getEnv();
        UserInfo fromUser = env.getFromUser();
        String msg = "用户:" + fromUser.getName() + "采纳了你的解答,您获得报酬: ￥" + money;
        return createBaseNotifyBuilder()
                .type(NotifyType.TASK)
                .content(getContent(msg, taskId))
                .action(Action.task_commit).build();
    }

    public Notify createTaskNotify(String s, Long taskId) {
        return createSystemNotifyBuilder()
                .type(NotifyType.TASK)
                .content(getContent(s, taskId))
                .action(Action.call).build();
    }

    public Notify createIllegalNotify(String msg, Long taskId) {
        msg = "因:" + msg + "。系统撤销了你的任务，请务必规范行为。";
        return createSystemNotifyBuilder()
                .type(NotifyType.TASK)
                .content(getContent(msg, taskId))
                .action(Action.call).build();
    }

    public Notify createWhyAgreeCancelNotify(String msg, Long taskId) {
        //告诉接单者 系统取消的原因
        msg = "因:" + msg + "。系统同意取消此次任务。";
        return createSystemNotifyBuilder()
                .type(NotifyType.TASK)
                .content(getContent(msg, taskId))
                .action(Action.call).build();
    }

    public Notify createCustomerServiceNotify(String content) {
        return createSystemNotifyBuilder()
                .type(NotifyType.CHAT)
                .content(getContent(content))
                .action(Action.call).build();
    }


    @Data
    public static class Env implements Serializable {
        private Long fromUserId;
        private Long toUserId;
        private String content;
        private UserInfo fromUser;
        private UserInfo toUser;
        private Boolean isSystem = false;
        private Boolean toAllUser = false;
        public Env(){}
        public Env(Long fromUserId, Long toUserId){
            Objects.requireNonNull(fromUserId);
            Objects.requireNonNull(toUserId);
            this.fromUserId = fromUserId;
            this.toUserId = toUserId;
            fromUser = NotifyUser.getInstance(fromUserId);
            toUser = NotifyUser.getInstance(toUserId);
        }
    }
    public NotifyFactory init(Long fromUserId, Long toUserId){
        Env env = new Env(fromUserId, toUserId);
        envs.set(env);
        return this;
    }
    public Env getEnv(){
        return envs.get();
    }
    private Notify.NotifyBuilder createBaseNotifyBuilder(){
        Env env = getEnv();
        Objects.requireNonNull(env.fromUser);
        return Notify.builder()
                .isRead(false)
                .createdTime(new Date())
                .fromUser(getEnv().fromUser)
                .fromUserId(getEnv().fromUserId)
                .toUser(getEnv().toUser)
                .toUserId(getEnv().toUserId);
    }
    private Notify.NotifyBuilder createSystemNotifyBuilder(){
        return Notify.builder()
                .isRead(false)
                .createdTime(new Date())
                .fromUser(getEnv().fromUser)
                .fromUserId(0l)
                .toUser(getEnv().toUser)
                .toUserId(getEnv().toUserId);
    }


    private String getContent(String msg, Long taskId, Long userId){
        NotifyContent build = NotifyContent.builder()
                .msg(msg)
                .taskId(taskId)
                .userId(userId)
                .build();
        return NotifyContent.toJSON(build);
    }
    private String getContent(String msg, Long taskId){
        return getContent(msg, taskId, null);
    }
    private String getContent(String msg){
        return getContent(msg, null, null);
    }

    public Notify createSystemNotify(String msg) {
        return createSystemNotifyBuilder().toUserId(0l)
                .toUser(null)
                .fromUserId(0l)
                .fromUser(null)
                .action(Action.call)
                .id(new Date().getTime())
                .type(NotifyType.REMIND)
                .content(msg).build();
    }

    public Notify createLikeNotify(){
        String msg = "嘿,我关注了你~";
        return createBaseNotifyBuilder()
                .type(NotifyType.REMIND)
                .action(Action.like)
                .content(getContent(msg, null, null))
                .build();

    }
    public Notify createReplyNotify(String msg, Long taskId){
        return createSystemNotifyBuilder()
                .type(NotifyType.TASK)
                .content(getContent(msg, taskId))
                .action(Action.reply).build();

    }
    public Notify createAddMoneyNotify(Long money){
        String msg = "充值成功,您的账户余额为 ￥" + (money.doubleValue()/100);
        return createSystemNotifyBuilder()
                .type(NotifyType.REMIND)
                .content(getContent(msg))
                .action(Action.add_money).build();

    }

    public Notify createTaskCanceledNotify(Long taskId){
        String msg = "任务编号为："+taskId+"的任务取消或被拒绝接收了";
        return createSystemNotifyBuilder()
                .type(NotifyType.TASK)
                .content(getContent(msg, taskId))
                .action(Action.task_canceled).build();
    }
    public Notify createTaskConfirmedNotify(Long taskId, Long money){
        String msg = "任务编号为: "+taskId+"的任务完成,您获得报酬: ￥" +money;
        return createSystemNotifyBuilder()
                .type(NotifyType.TASK)
                .content(getContent(msg, taskId))
                .action(Action.task_confirmed).build();
    }
    public Notify createTaskTakenNotify(Long taskId){
        String msg = "您好，我接收了你的任务";
        return createBaseNotifyBuilder()
                .type(NotifyType.TASK)
                .content(getContent(msg, taskId))
                .action(Action.task_taken).build();
    }
    public Notify createWasIssuedNotify(Long taskId, String title){
//        String msg = "用户 " + getEnv().fromUser.getName() + " 请求您帮TA完成任务("+title+")~";
        String msg = "hey, 能帮我做个任务吗?";
        return createBaseNotifyBuilder()
                .type(NotifyType.TASK_OPERATE)
                .content(getContent(msg, taskId, getEnv().fromUser.getId()))
                .action(Action.was_issued).build();
    }
    public Notify createChatNotify(String content){
        return createBaseNotifyBuilder()
                .type(NotifyType.CHAT)
                .content(getContent(content))
                .action(Action.call).build();
    }
    public void remove(){
        envs.remove();
    }
}