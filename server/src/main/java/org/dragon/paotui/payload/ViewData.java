package org.dragon.paotui.payload;

import lombok.Getter;
import org.dragon.paotui.enumeration.ErrorResp;
import org.dragon.paotui.pojo.Notify;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Getter
public class ViewData<T> implements Serializable {
    private Integer errCode = 0;//default 0 show success
    private String msg = "success";
    private T data;

    public ViewData(){
    }
    public ViewData(T data){
        this.data = data;
    }
    public ViewData(Integer errCode, String msg){
        this.errCode = errCode;
        this.msg = msg;
    }
    public ViewData(Integer errCode, String msg, T data){
        this.errCode = errCode;
        this.msg = msg;
        this.data = data;
    }
    public ViewData(Integer errCode, T data){
        this.errCode = errCode;
        this.data = data;
    }

    public static <T> ViewData<T> ok(){
        return new ViewData<T>();
    }

    public static <T> ViewData<T> ok(T data){
        return new ViewData<T>(data);
    }

    public static <T> ViewData<T> error(String msg){
        return new ViewData<T>(-1, msg);
    }

    public static <T> ViewData<T> error(Integer errCode, String msg){
        return new ViewData<T>(errCode, msg);
    }

    public static <T> ViewData<T> error(ErrorResp errorResp){
        return new ViewData<T>(errorResp.getCode(),errorResp.getErrMsg());
    }

    public static <T> ViewData<T> ok(Integer errCode, T data) {
        return new ViewData<T>(errCode,data);
    }
}
