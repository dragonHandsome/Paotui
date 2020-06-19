package org.dragon.paotui.enumeration;

public enum  TaskDealStatus {
    PENDING(1,"等待处理"),
    QUICK_DEAL(2,"急需处理"),
    COMPLETE(3,"完成处理")
    ;
    private Integer status;
    private String msg;
    TaskDealStatus(int status, String info) {
        this.status = status;
        this.msg = info;
    }
}
