package org.dragon.paotui.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class MyDateFormat {
    public static Date formatDate(String yyMMdd){
        Date date = null;
        try{
            LocalDate parse = LocalDate.parse(yyMMdd);
            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zdt = parse.atStartOfDay(zoneId);
            date = Date.from(zdt.toInstant());
        }catch (Exception e){
            MyLogUtil.throwError("日期转换异常");
        }
        return date;
    }
}
