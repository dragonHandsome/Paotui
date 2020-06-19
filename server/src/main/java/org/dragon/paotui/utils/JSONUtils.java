package org.dragon.paotui.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dragon.paotui.payload.JwtTakenData;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
public class JSONUtils {
    public static String toJSON(Object t){
        String s = null;
        try {
            s = getMapper().writeValueAsString(t);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("json数据转换异常");
        }
        return s;
    }
    public static <T> T toObj(String json, Class<T> clazz){
        T t = null;
        try {
            t = getMapper().readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("数据转json异常");
        }
        return t;
    }
    static ObjectMapper getMapper(){
        return new ObjectMapper();
    }
}
