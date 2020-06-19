package org.dragon.paotui.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class MyLogUtil {
    static HttpServletRequest getRequest(){
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }
    public static void error(String msg){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        String className = stackTraceElement.getClassName();
        String method = stackTraceElement.getMethodName();
        log.warn("ip: {}, position:{}.{}, msg: {}", getRequest().getRemoteAddr(),className, method, msg);
        throw new RuntimeException(msg);
    }
    public static void warn(String msg){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        String className = stackTraceElement.getClassName();
        String method = stackTraceElement.getMethodName();
        System.out.println(getRequest().getRemoteAddr() + className + method + ":" + msg);
        log.warn("ip: {}, position:{}.{}, msg: {}", getRequest().getRemoteAddr(),className, method, msg);
    }
    public static void throwError(String msg){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        String className = stackTraceElement.getClassName();
        String method = stackTraceElement.getMethodName();
        String info = className+"."+method + "  msg:" + msg;
        log.warn(info);
        throw new RuntimeException(info);
    }
}
