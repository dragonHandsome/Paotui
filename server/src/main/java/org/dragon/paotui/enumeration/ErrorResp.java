package org.dragon.paotui.enumeration;

import lombok.Getter;

@Getter
public enum  ErrorResp {
    SIGN_UP_ERROR(-1, "登录失败"),
    UPDATE_USER_ERROR(-2, "更新用户失败"),
    UPDATE_USER_DETAIL_ERROR(-3, "更新详细用户失败"),
    INSERT_ERROR(-5, "添加失败"),
    UPDATE_ERROR(-6,"更新失败"),
    GET_INFO_ERROR(-4,"获取信息失败"),
    LIKE_ERROR(-7,"关注失败"),
    NOT_MORE(-9,"没有更多了"),
    ADD_MONEY_ERROR(-10, "充值失败"),
    TOKEN_ERROR(-11, "未登录或者验证过期"),
    ;
    private Integer code;
    private String errMsg;
    ErrorResp(Integer code, String errMsg) {
        this.code  = code;
        this.errMsg = errMsg;
    }
}
