package org.dragon.paotui.enumeration;

public enum Action {
    like(1, "被关注"),
    reply(2, "被回复"),
    add_money(3, "充值成功"),
    task_confirmed(4, "通知任务已完成"),
    task_canceled(5, "通知任务取消了"),
    task_taken(6, "通知任务被接收了"),
    task_commit(8,"接单用户完成任务"),
    was_issued(7, "被发布了任务，决定是否接收"),
    call(8,"聊天")
    ;
    private Integer code;
    private String msg;
    Action(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
