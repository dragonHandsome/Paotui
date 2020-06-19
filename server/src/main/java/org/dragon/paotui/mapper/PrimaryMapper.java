package org.dragon.paotui.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface PrimaryMapper {
    @Select("select LAST_INSERT_ID()")
    Long lastInsertPrimary();
}
