package org.dragon.paotui.enumeration;

import lombok.Getter;

@Getter
public enum TaskStatus {
    AWAIT_TAKEN(1, "等待接任务"),
    AWAIT_COMMIT(2, "等待提交"),
    AWAIT_CONFIRM(3, "等待确认"),
    REQ_CANCEL(4, "请求取消"),
    COMPLETE(5, "已完成"),
    CANCELED(6, "取消"),
    //EXPIRED(5, "过期"),
    ;
    private Integer status;
    private String info;
    TaskStatus(Integer status, String info) {
        this.status = status;
        this.info = info;
    }
}
