package org.dragon.paotui.mapper;

import org.apache.ibatis.annotations.*;
import org.dragon.MyMapper;
import org.dragon.paotui.pojo.WechatOption;
import org.dragon.paotui.pojo.WechatTask;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Mapper
//InsertListMapper
public interface WechatTaskMapper extends MyMapper<WechatTask> {

    @Select("select LAST_INSERT_ID()")
    Long lastInsertPrimary();
    @Insert({
            "<script>",
            "insert into wechat_task_option(value, option_id, task_id ) values ",
            "<foreach collection='wechatOptions' item='item' index='index' separator=','>",
            "(#{item.value}, #{item.id}, #{taskId})",
            "</foreach>",
            "</script>"
    })
    void insertTaskOptions(@Param("wechatOptions") List<WechatOption> wechatOptions, @Param("taskId") Long taskId);

    @Select("SELECT * FROM wechat_option a, wechat_task_option b WHERE a.id = b.option_id AND task_id = #{taskId}")
    Set<WechatOption> getTaskOptions(Long taskId);


    @Update("update wechat_task_option set value = #{wechatOption.value} where task_id = #{taskId} option_id = #{wechatOption.id}")
    void updateOptions(@Param("wechatOption") WechatOption wechatOption, @Param("taskId") Long taskId);

    @Select("SELECT * FROM wechat_task WHERE STATUS != 'CANCELED' AND STATUS != 'COMPLETE' AND DATE_SUB(#{date}, INTERVAL #{intervalDay} DAY) > create_time")
    List<WechatTask> findUnfinishedTaskByIntervalDay(Date date, Integer intervalDay);
}
