package org.dragon.paotui.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.dragon.paotui.payload.Statistics;

import java.util.List;

@Mapper
public interface StatisticsMapper {
    //#获取总的根据种类
    @Select("SELECT COUNT(1) AS 'count', NAME AS category\n" +
            "FROM wechat_task a, wechat_category b\n" +
            "WHERE a.`category_id` = b.id\n" +
            "GROUP BY NAME;")
    List<Statistics.CountAllTaskGroupByCategory> CountAllTaskGroupByCategory();
    //#获取上周根据种类和星期几的单数
    @Select("SELECT COUNT(1) AS 'count', NAME AS category,\n" +
            "DATE_FORMAT(create_time, '%a') AS DATE \n" +
            "FROM wechat_task a, wechat_category b\n" +
            "WHERE a.`category_id` = b.id\n" +
            "AND DATE_FORMAT(create_time, '%u') = DATE_FORMAT(NOW(), '%u') - 1\n" +
            "GROUP BY category, DATE;")
    List<Statistics.CountAllTaskGroupByLastWeekDayAndCategory> CountAllTaskGroupByLastWeekDayAndCategory();
    //#获取上周根据是否有钱和星期几的任务数
    @Select("SELECT COUNT(1) AS 'count',\n" +
            "CASE reward_money \n" +
            "WHEN 0 THEN 0\n" +
            "ELSE 1\n" +
            "END has_reward,\n" +
            "DATE_FORMAT(create_time, '%a') AS DATE \n" +
            "FROM wechat_task \n" +
            "WHERE\n" +
            "DATE_FORMAT(create_time, '%u') = DATE_FORMAT(NOW(), '%u') - 1 \n" +
            "GROUP BY has_reward, DATE;\n")
    List<Statistics.CountAllTaskGroupByLastWeekDayAndHasreward> CountAllTaskGroupByLastWeekDayAndHasreward();
}
