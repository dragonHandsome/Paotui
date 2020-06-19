package org.dragon.paotui.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.dragon.MyMapper;
import org.dragon.paotui.pojo.WechatCategory;
import org.dragon.paotui.pojo.WechatOption;

import java.util.List;

@Mapper
public interface CategoryMapper extends MyMapper<WechatCategory> {

    @Select("SELECT * FROM wechat_option WHERE id IN " +
            "(SELECT option_id FROM wechat_category_option" +
            " WHERE category_id = #{categoryId}) ")
    List<WechatOption> findOptionsByCategoryId(Long categoryId);
}
