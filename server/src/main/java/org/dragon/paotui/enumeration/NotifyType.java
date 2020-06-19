package org.dragon.paotui.enumeration;

public enum  NotifyType {
    REMIND(1, "提醒"),
    TASK(2, "任务状态提醒"),
    CHAT(3, "聊天"),
    TASK_OPERATE(4, "用户手动处理任务类")
    ;
    private Integer code;
    private String msg;
    NotifyType(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
